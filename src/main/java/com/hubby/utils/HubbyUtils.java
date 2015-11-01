package com.hubby.utils;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.GL11;
import org.reflections.Reflections;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hubby.network.HubbyNetworkHelper;
import com.hubby.utils.HubbyConstants.ArmorType;
import com.hubby.utils.HubbyConstants.ChestType;
import com.hubby.utils.HubbyConstants.Direction;
import com.hubby.utils.HubbyConstants.HubbyClientPacketType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDoubleWoodSlab;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class stores a number of static methods that perform useful tasks making
 * life a little bit easier
 * 
 * @author davidleistiko
 */
public class HubbyUtils {

    /**
     * Enumerate the options for the draw gradient method
     * @author davidleistiko
     */
    public enum GradientMode {
        HORIZONTAL, VERTICAL
    }

    /**
     * Identifies that describe the type of rendering we are registering a
     * particular item for
     */
    public static final String NORMAL_MODEL = "normal";
    public static final String INVENTORY_MODEL = "inventory";
    public static final String DEFAULT_BLOCK_RESOURCE = "models/block/grass_normal";
    public static final String DEFAULT_ITEM_RESOURCE = "models/item/diamond_sword";

    /**
     * Stores all registered key-bindings and uses these to detect when the user
     * has pressed a key
     */
    public static final HashMap<String, KeyBinding> KEY_BINDINGS = new HashMap<String, KeyBinding>(32);
    
    /**
     * This value can be set to apply as a scale to the x,y,w,h values when drawing
     * a textured rect. After this value is set, when the first call to draw a textured
     * rect is made, the scale value is taken into account and then is reset to a value
     * of 1 so that the next call is unaffected by the scale unless the user specifically
     * sets it
     */
    private static float _drawTexturedRectScale = 1.0f;
    

    /**
     * Sets the scale value to use with any calls made to
     * draw a textured rect
     * @param scale - the scale to use
     */
    public static void setDrawTexturedRectScale(float scale) {
        _drawTexturedRectScale = scale;
    }
    
    /**
     * Resets the scale value to use when drawing textured rects
     */
    public static void clearDrawTexturedRectScale() {
        setDrawTexturedRectScale(1.0f);
    }
    
    /**
     * Simple helper function to get full path for a mod item
     * @param modId - the name of the mod
     * @param path - the sub-path to the resource
     * @return String - the full qualified mod resource name
     */
    public static final String getResourceLocation(String modId, String path) {
        return modId + ":" + path;
    }

    /**
     * This helper methods registers an item with the registires and renderers
     * to ensure that the item can be properly rendered in-game
     * @param obj - the item to be registered
     */
    public static <T extends Item & HubbyNamedObjectInterface> void registerNamedItem(String modID, T item) {
        GameRegistry.registerItem(item, item.getName());
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        renderItem.getItemModelMesher().register(item, 0, new ModelResourceLocation(HubbyUtils.getResourceLocation(modID, item.getName()), INVENTORY_MODEL));
    }

    /**
     * Registers a 'KeyBinding' which we can use to determine if the user is
     * currently pressing any button
     * @param alias - the id name for the key
     * @param binding - the actual binding
     */
    public static void regiterKeyBinding(String alias, KeyBinding binding) {
        ClientRegistry.registerKeyBinding(binding);
        KEY_BINDINGS.put(alias, binding);
    }

    /**
     * Returns a list of all pressed keys currently held down by the user
     * @return ArrayList - the list of all pressed keys
     */
    public static ArrayList<String> getPressedKeys() {
        ArrayList<String> pressed = new ArrayList<String>(4);

        for (String alias : KEY_BINDINGS.keySet()) {
            if (KEY_BINDINGS.get(alias).isPressed()) {
                pressed.add(alias);
            }
        }

        return pressed;
    }

    /**
     * Returns the isPressed result for the key that is identified by name
     * @param alias - the key for the key-binding
     * @return boolean - is the key pressed?
     */
    public static boolean isKeyPressed(String alias) {
        KeyBinding binding = KEY_BINDINGS.get(alias);
        return binding != null ? binding.isPressed() : false;
    }

    /**
     * Helper function for drawing a textured rectangle
     * @param zLevel - the depth of the rectangle in the scene
     * @param posX - the left most position
     * @param posY - the top most position
     * @param width - the width
     * @param height - the height
     * @param u1 - the left texture coord
     * @param v1 - the top texture coord
     * @param u2 - the right texture coord
     * @param v2 - the bottom texture coord
     * @param color - the color to apply
     */
    public static void drawTexturedRectHelper(float zLevel, int posX, int posY, int width, int height, int u1, int v1, int u2, int v2, HubbyColor color) {
        float f = 0.00390625F * _drawTexturedRectScale;
        float f1 = 0.00390625F * _drawTexturedRectScale;
        clearDrawTexturedRectScale();
        
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().setColorRGBA_F(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        tessellator.getWorldRenderer().addVertexWithUV((double) (posX + 0), (double) (posY + height), (double) zLevel, (double) ((float) (u1) * f), (double) ((float) (v2) * f1));
        tessellator.getWorldRenderer().addVertexWithUV((double) (posX + width), (double) (posY + height), (double) zLevel, (double) ((float) (u2) * f), (double) ((float) (v2) * f1));
        tessellator.getWorldRenderer().addVertexWithUV((double) (posX + width), (double) (posY + 0), (double) zLevel, (double) ((float) (u2) * f), (double) ((float) (v1) * f1));
        tessellator.getWorldRenderer().addVertexWithUV((double) (posX + 0), (double) (posY + 0), (double) zLevel, (double) ((float) (u1) * f), (double) ((float) (v1) * f1));
        tessellator.draw();
    }
    
    /**
     * Helper function for drawing a textured rectangle
     * @param zLevel - the depth of the rectangle in the scene
     * @param posX - the left most position
     * @param posY - the top most position
     * @param width - the width
     * @param height - the height
     * @param Rectangle - the uvs
     * @param HubbyColor - the color to use
     */
    public static void drawTexturedRectHelper(float zLevel, int posX, int posY, int width, int height, Rectangle uvs, HubbyColor color) {
        int u1 = (int)uvs.getMinX();
        int u2 = (int)uvs.getMaxX();
        int v1 = (int)uvs.getMinY();
        int v2 = (int)uvs.getMaxY();
        drawTexturedRectHelper(zLevel, posX, posY, width, height, u1, v1, u2, v2, color);
    }
    
    /**
     * Helper function for drawing a textured rectangle
     * @param zLevel - the depth of the rectangle in the scene
     * @param posX - the left most position
     * @param posY - the top most position
     * @param width - the width
     * @param height - the height
     * @param u1 - the left texture coord
     * @param v1 - the top texture coord
     * @param u2 - the right texture coord
     * @param v2 - the bottom texture coord
     */
    public static void drawTexturedRectHelper(float zLevel, int posX, int posY, int width, int height, int u1, int v1, int u2, int v2) {
        float f = 0.00390625F * _drawTexturedRectScale;
        float f1 = 0.00390625F * _drawTexturedRectScale;
        clearDrawTexturedRectScale();
        
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().addVertexWithUV((double) (posX + 0), (double) (posY + height), (double) zLevel, (double) ((float) (u1) * f), (double) ((float) (v2) * f1));
        tessellator.getWorldRenderer().addVertexWithUV((double) (posX + width), (double) (posY + height), (double) zLevel, (double) ((float) (u2) * f), (double) ((float) (v2) * f1));
        tessellator.getWorldRenderer().addVertexWithUV((double) (posX + width), (double) (posY + 0), (double) zLevel, (double) ((float) (u2) * f), (double) ((float) (v1) * f1));
        tessellator.getWorldRenderer().addVertexWithUV((double) (posX + 0), (double) (posY + 0), (double) zLevel, (double) ((float) (u1) * f), (double) ((float) (v1) * f1));
        tessellator.draw();
    }
    
    /**
     * Helper function for drawing a textured rectangle
     * @param zLevel - the depth of the rectangle in the scene
     * @param posX - the left most position
     * @param posY - the top most position
     * @param width - the width
     * @param height - the height
     * @param Rectangle - the uvs
     */
    public static void drawTexturedRectHelper(float zLevel, int posX, int posY, int width, int height, Rectangle uvs) {
        int u1 = (int)uvs.getMinX();
        int u2 = (int)uvs.getMaxX();
        int v1 = (int)uvs.getMinY();
        int v2 = (int)uvs.getMaxY();
        drawTexturedRectHelper(zLevel, posX, posY, width, height, u1, v1, u2, v2);
    }

    /**
     * Helper method that will draw a rectangle filled with a gradient color
     * scheme using the minecraft built-in tessellator. Gradient can be either
     * horizontal or vertical
     * @param mode - the gradient mode (vertical/horizontal)
     * @param colorOne - the first color for the gradient
     * @param colorTwo - the second color for the gradient
     * @param left - the left x position
     * @param top - the top y position
     * @param right - the right x position
     * @param bottom - the bottom y position
     */
    public static void drawGradientRectHelper(GradientMode mode, HubbyColor colorOne, HubbyColor colorTwo, double left, double top, double right, double bottom) {
        float r1 = colorOne.getRed();
        float g1 = colorOne.getGreen();
        float b1 = colorOne.getBlue();
        float a1 = colorOne.getAlpha();
        float r2 = colorTwo.getRed();
        float g2 = colorTwo.getGreen();
        float b2 = colorTwo.getBlue();
        float a2 = colorTwo.getAlpha();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(770, 771);// 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();

        if (mode == GradientMode.HORIZONTAL) {
            tessellator.getWorldRenderer().setColorRGBA_F(r1, g1, b1, a1);
            tessellator.getWorldRenderer().addVertex(left, top, 0.0d);
            tessellator.getWorldRenderer().addVertex(left, bottom, 0.0d);
            tessellator.getWorldRenderer().setColorRGBA_F(r2, g2, b2, a2);
            tessellator.getWorldRenderer().addVertex(right, bottom, 0.0d);
            tessellator.getWorldRenderer().addVertex(right, top, 0.0d);
        }
        else if (mode == GradientMode.VERTICAL) {
            tessellator.getWorldRenderer().setColorRGBA_F(r1, g1, b1, a1);
            tessellator.getWorldRenderer().addVertex(right, top, 0.0d);
            tessellator.getWorldRenderer().addVertex(left, top, 0.0d);
            tessellator.getWorldRenderer().setColorRGBA_F(r2, g2, b2, a2);
            tessellator.getWorldRenderer().addVertex(left, bottom, 0.0d);
            tessellator.getWorldRenderer().addVertex(right, bottom, 0.0d);
        }

        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /**
     * Helper function that adds liquid that was scooped up into the bucket that
     * scooped it
     * @param world - the world
     * @param pos - the position to consider
     * @return ItemStack - the ItemStack containing the filled bucket item (or null otherwise)
     */
    @SuppressWarnings("unused")
    public static ItemStack fillBucketWithLiquid(World world, MovingObjectPosition pos) {

        ItemBucket bucket = null;
        Block block = world.getBlockState(pos.getBlockPos()).getBlock();

        // TODO:
        // Fix this
        // if (block instanceof NitroBlockFluid && block.getMaterial() ==
        // NitroInterface.nitroFluidMaterial) {
        if (true) {
            bucket = null; // NitroInterface.nitroBucketFluid;
        }
        else if (block == Blocks.lava || block == Blocks.flowing_lava) {
            bucket = (ItemBucket) Items.lava_bucket;
        }
        else if (block == Blocks.water || block == Blocks.flowing_water) {
            bucket = (ItemBucket) Items.water_bucket;
        }

        // TODO:
        // Is this the right way to get the metadata from a block?
        if (bucket != null && block.getMetaFromState(world.getBlockState(pos.getBlockPos())) == 0) {
            world.setBlockToAir(pos.getBlockPos());
            return new ItemStack(bucket);
        }

        return null;
    }

    /**
     * Returns an instance of the player on the client
     * @return EntityPlayer - the player for the client
     */
    @SideOnly(Side.CLIENT)
    public static EntityPlayerSP getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    /**
     * Returns an instance of the world on the client
     * @return World - the client world
     */
    @SideOnly(Side.CLIENT)
    public static WorldClient getClientWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    /**
     * Are we currently running in single player mode?
     * @return boolean - is single-player
     */
    public static boolean isSinglePlayer() {
        return Minecraft.getMinecraft().getIntegratedServer() != null && MinecraftServer.getServer().isDedicatedServer() == false;
    }

    /**
     * Return the current number of players playing
     * @return int - the number of players active right now
     */
    @SideOnly(Side.SERVER)
    public static int getPlayerCount() {
        return MinecraftServer.getServer().getCurrentPlayerCount();
    }

    /**
     * Return the maximum number of players allowed
     * @return int - the max player count
     */
    @SideOnly(Side.SERVER)
    public static int getMaxPlayerCount() {
        return MinecraftServer.getServer().getMaxPlayers();
    }

    /**
     * Returns a list of all players currently on the server
     * @return ArrayList - the list of all multiplayer player entities
     */
    public static ArrayList<EntityPlayerMP> getAllPlayers() {
        ArrayList<EntityPlayerMP> players = new ArrayList<EntityPlayerMP>();
        for (World w : MinecraftServer.getServer().worldServers) {
            ListIterator it = w.playerEntities.listIterator();
            while (it.hasNext()) {
                Object listObject = it.next();
                EntityPlayerMP player = listObject instanceof EntityPlayerMP ? (EntityPlayerMP) listObject : null;
                if (player != null) {
                    players.add(player);
                }
            }
        }

        return players;
    }

    /**
     * Return the name of the world
     * @return String - the world name
     */
    public static String getWorldName() {
        return HubbyUtils.getClientWorld().getWorldInfo().getWorldName();
    }

    /**
     * Returns the server-side version of the client player
     * @return EntityPlayerMP - the server-side version of the client player
     */
    public static EntityPlayerMP getServerPlayer() {
        if (Minecraft.getMinecraft().thePlayer != null) {
            UUID id = Minecraft.getMinecraft().thePlayer.getUniqueID();
            return MinecraftServer.getServer().getConfigurationManager().getPlayerByUUID(id);
        }
        return null;
    }

    /**
     * Returns the server-side version of the world
     * @return WorldServer - the world on the server
     */
    public static WorldServer getServerWorld() {
        EntityPlayerMP serverPlayer = HubbyUtils.getServerPlayer();
        if (serverPlayer != null) {
            return serverPlayer.getServerForPlayer();
        }
        return null;
    }

    /**
     * Returns if we are running the server code
     * @return boolean - are we the server side?
     */
    public static boolean isServerSide() {
        return !HubbyUtils.getClientWorld().isRemote;
        // return MinecraftServer.getServer().isDedicatedServer();
    }

    /**
     * Returns if we are running on the client
     * @return boolean - are we running the client?
     */
    public static boolean isClientSide() {
        return !HubbyUtils.isServerSide();
    }

    /**
     * Check if we are on the client based on the world passed in
     * @param world - The <code>World</code> to check
     * @return boolean - true if we are the client
     */
    public static boolean isClientSide(World world) {
        return world.isRemote;
    }

    /**
     * Check if we are the client world or not
     * @param world - the block access
     * @return boolean - are we the client world
     */
    public static boolean isClientSide(IBlockAccess world) {
        return world.equals(HubbyUtils.getClientWorld());
    }

    /**
     * Returns the current date in string format
     * @return String - the current date
     */
    public static String getCurrentDateString() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS MM/dd/yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    /**
     * Returns the UTC time for right now
     * @return long - the UTC time
     */
    public static long getTimestamp() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        int utcOffset = c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET);
        return c.getTimeInMillis() + utcOffset;
    }

    /**
     * Utility function that converts a raw utc timestamp value
     * into a human readable string that expresses the value for
     * the current date
     * @param timeStamp - the utc timestamp
     * @return String - the date string
     */
    public static String convertToDateString(Long timestamp) {
        // first set the time on the calendar to be equal to our utc
        // timestamp
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        // print out calendar time using formatting
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS MM/dd/yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    /**
     * Converts a human readable date string into a utc timestamp
     * @param dateStr - the date string to parse
     * @return Long - the representation of the date passed in as a utc timestamp (returns -1 on error)
     * @throws ParseException
     */
    public static Long convertToTimestamp(String dateStr) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS MM/dd/yyyy");
            Date date = dateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            int utcOffset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
            return calendar.getTimeInMillis() + utcOffset;
        }
        catch (Exception e) {
            return -1L;
        }
    }

    /**
     * Checks if the entity is currently standing in some sort of
     * fluid/liquid
     * @param entity - the entity to check
     * @param mat - the material to check
     * @return boolean - is the entity inside of the material?
     */
    public static boolean isEntityStandingInMaterial(EntityLivingBase entity, Material mat) {
        return entity.worldObj.isMaterialInBB(entity.getEntityBoundingBox().expand(-0.1f, -0.4f, -0.1f), mat);
    }

    /**
     * Checks if the entity in question is standing in any liquid. This is
     * achieved by using java's built-in reflection classes
     * @param entity - the entity to check
     * @return Material - the liquid material that the entity is standing in
     */
    public static Material isEntityStandingInAnyLiquid(EntityLivingBase entity, List<Material> additionalMaterials) {

        // Here we want to gather all Materials that are declared within the Material class
        final List<Material> materials = new ArrayList<Material>();
        Field[] allFields = Material.class.getDeclaredFields();

        // iterate over all fields and add any Materials that we
        for (Field f : allFields) {
            Class<?> fieldType = f.getType();
            if (fieldType.isAssignableFrom(Material.class)) {
                try {
                    // Here we pass in null since all of the Materials are static fields
                    Material mat = (Material) f.get(null);
                    materials.add(mat);
                }
                catch (Exception e) {
                    System.out.println("[HubbyUtils] Failed to lookup Material field within the Material class!");
                    e.printStackTrace();
                }
            }
        }

        // Here is where we would add any custom Materials that are a part
        // of the current mod
        if (additionalMaterials != null) {
            materials.addAll(additionalMaterials);
        }

        // This predicate will remove all Materials that are not liquids
        final Predicate<Material> predicate = new Predicate<Material>() {
            @Override
            public boolean apply(Material mat) {
                return mat.isLiquid();
            }
        };
        Iterable<Material> iter = Iterables.filter(materials, predicate);
        ArrayList<Material> filtered = Lists.newArrayList(iter.iterator());

        // query if entity is standing in any of the liquids that we discovered
        for (Material mat : filtered) {
            if (isEntityStandingInMaterial(entity, mat)) {
                return mat;
            }
        }

        // No luck! :(
        return null;
    }

    /**
     * Checks if the entity passed in is wearing the armor item identified
     * both by specific a <code>ItemArmor</code> class and the designated <code>ArmorType</code>
     * @param entity - the entity to check
     * @param klass - the class of armor
     * @param armor - the armor type
     * @return boolean - is the entity wearing the piece of armor
     */
    public boolean isWearingArmor(EntityLivingBase entity, Class<? extends ItemArmor> klass, ArmorType armor) {
        ItemStack armorItem = entity.getCurrentArmor(armor.getInventorySlot() - 1);
        return armorItem != null && klass.isInstance(armorItem.getItem());
    }

    /**
     * Checks if we are wearing a vanilla Minecraft armor with a specific <code>ToolMaterial</code>
     * @param entity - the <code>Entity</code> we are checking
     * @param material - the <code>ToolMaterial</code> to match against
     * @param armor - the specific piece of armor we are looking for
     * @return
     */
    public boolean isWearingVanillaArmor(EntityLivingBase entity, ToolMaterial material, ArmorType armor) {
        ItemStack armorItem = entity.getCurrentArmor(armor.getInventorySlot() - 1);
        return armorItem != null && ((ItemArmor) armorItem.getItem()).getArmorMaterial().name() == material.name();
    }

    /**
     * Checks if the entity passed in is wearing an entire set of armor
     * as identified by the armor class type passed in
     * @param entity - the entity to check
     * @param klass - the ItemArmor class to check
     * @return boolean - is the entity decked out in the specific armor?
     */
    public boolean isWearingWholeArmor(EntityLivingBase entity, Class<? extends ItemArmor> klass) {
        return isWearingArmor(entity, klass, ArmorType.HELMET) && isWearingArmor(entity, klass, ArmorType.CHESTPLATE) && isWearingArmor(entity, klass, ArmorType.LEGGINGS) && isWearingArmor(entity, klass, ArmorType.BOOTS);
    }

    /**
     * Checks if we are wearing a vanilla Minecraft armor with a specific <code>ToolMaterial</code>
     * @param entity - the <code>Entity</code> we are checking
     * @param material - the <code>ToolMaterial</code> to match against
     * @return boolean - are we wearing the entire vanilla armor set?
     */
    public boolean isWearingWholeVanillaArmor(EntityLivingBase entity, ToolMaterial material) {
        return isWearingVanillaArmor(entity, material, ArmorType.HELMET) && isWearingVanillaArmor(entity, material, ArmorType.CHESTPLATE) && isWearingVanillaArmor(entity, material, ArmorType.LEGGINGS) && isWearingVanillaArmor(entity, material, ArmorType.BOOTS);
    }

    /**
     * Gives the specified armor to the entity passed in
     * @param entity - the entity to give the armor to
     * @param armor - the armor to give to the entity
     * @param type - the type of armor that is being applied
     */
    public static <T extends ItemArmor> void addArmorToEntity(EntityLivingBase entity, T armor) {
        ArmorType type = ArmorType.values()[armor.armorType + 1];
        ItemStack stack = new ItemStack(armor, 1);
        entity.setCurrentItemOrArmor(type.getInventorySlot(), stack);
    }

    /**
     * Adds the generic Minecraft armor identified by the <code>ToolMaterial</code>
     * passed in
     * @param entity - the <code>Entity</code> to apply the armor to
     * @param armorMaterial - the material of the armor we want to add
     * @return boolean - did we add any armor?
     */
    public static boolean addFullVanillaArmorToEntity(EntityLivingBase entity, ToolMaterial armorMaterial) {

        // the itemRegistry that is a map containing all of the generic Minecraft items
        boolean success = false;
        List<RegistryNamespaced> allRegistries = HubbyUtils.searchForFieldsOfType("net.minecraft.item", Item.class, null, RegistryNamespaced.class);

        // This shouldn't happen, but who know, maybe Mojang will change how
        // they register their items... for now, we are good to assume that
        // the registry we are looking for will be the only entry in this list
        if (allRegistries.size() == 0) {
            return false;
        }

        RegistryNamespaced registry = (RegistryNamespaced) allRegistries.get(0);
        Set keys = registry.getKeys();

        // Iterate over all of the item keys, looking for armor
        for (Object key : keys) {
            Item item = (Item) registry.getObject(key);
            if (ItemArmor.class.isInstance(item)) {
                ItemArmor armorItem = (ItemArmor) item;

                // Make sure that the armor is of the correct material
                if (armorItem.getArmorMaterial().name() == armorMaterial.name()) {
                    HubbyUtils.addArmorToEntity(entity, (ItemArmor) item);
                    success = true;
                }
            }
        }

        return success;
    }

    /**
     * Searches for all fields belonging to the <code>sourceClass</code> that is
     * contained within the package identified by <code>packageName</code>
     * @param packageName - the package to look in
     * @param sourceClass - the source class to search
     * @param instance - the instance to lookup the fields on (can be null)
     * @param searchClass - the class to match against
     * @return
     */
    public static <T> List<T> searchForFieldsOfType(String packageName, final Class sourceClass, final Object instance, final Class searchClass) {

        final List<T> foundObjects = new ArrayList<T>();

        // Lets search for all fields contained in the named package
        // that belong to the class passed in
        Reflections r = new Reflections(packageName);
        Set<Field> fields = r.getAllFields(sourceClass, new Predicate<Field>() {
            @Override
            public boolean apply(Field f) {
                try {
                    // Check each field to see the object matches the searchClass
                    Object obj = f.get(instance);
                    if (obj.getClass().isAssignableFrom(searchClass) || searchClass.isInstance(obj)) {
                        foundObjects.add((T) obj);
                        return true;
                    }
                    return false;
                }
                catch (Exception e) {
                    return false;
                }
            }
        });

        // Return all objects that were found that were of mathcin class type
        return foundObjects;
    }

    /**
     * Attempts to look at the block directly beneath the entity passed in
     * @param entity - the entity to check
     * @return Block - the block beneath the entity
     */
    public static HubbyBlockResult findBlockUnderEntity(Entity entity) {
        int blockX = MathHelper.floor_double(entity.posX);
        int blockY = MathHelper.floor_double(entity.getEntityBoundingBox().minY);
        int blockZ = MathHelper.floor_double(entity.posZ);
        BlockPos pos = new BlockPos(blockX, blockY, blockZ);
        Block block = entity.worldObj.getBlockState(pos).getBlock();
        Item blockItem = Item.getItemFromBlock(block);
        HubbyBlockResult result = (block != null || blockItem != null) ? new HubbyBlockResult(pos) : new HubbyBlockResult();

        // search for the first non-air block below us if indeed
        // our current underneath block is air
        int yOffset = -1;
        while (blockItem == null && pos.getY() >= 0 && block == Blocks.air) {
            BlockPos offsetPos = pos.add(0, yOffset, 0.0f);
            block = HubbyUtils.getServerWorld().getBlockState(offsetPos).getBlock();
            if (block != null) {
                blockItem = Item.getItemFromBlock(block);
                result.setBlockPos(offsetPos);
            }
            yOffset -= 1;
        }

        return result;
    }

    /**
     * Finds the next block by offseting from the current block result position
     * @param currentBlock - the current block to offset from
     * @param offset - the offset to apply to the position
     * @param allowAir - do we allow air blocks?
     * @return HubbyBlockResult - the block result for the position with the offset
     */
    public static HubbyBlockResult getNextBlock(HubbyBlockResult currentBlock, BlockPos offset, boolean allowAir) {
        // get starting pos
        BlockPos iterPos = new BlockPos(0, 0, 0);
        if (currentBlock != null) {
            iterPos = currentBlock.getBlockPos();
        }

        // add the offset to the block pos
        iterPos = iterPos.add(offset);

        // get the block at the position and return result
        IBlockState state = HubbyUtils.getClientWorld().getBlockState(iterPos);
        Block block = state != null ? state.getBlock() : null;
        if (block != null && (block != Blocks.air || allowAir)) {
            HubbyBlockResult result = new HubbyBlockResult(iterPos);
            return result;
        }
        return new HubbyBlockResult();
    }

    /**
     * Returns the entity facing direction based on their current rotation
     * @param entity - the entity to check
     * @return Direction - the direction they are facing
     */
    public static Direction getFacingDirection(Entity entity) {
        int d = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360) + 0.50) & 3;
        return Direction.values()[d];
    }

    /**
     * Attempts to get the block the user is looking at now
     * @return Block - the <code>Block</code> being looked at (null if none)
     */
    public static Block getLookAtBlock() {
        MovingObjectPosition pos = Minecraft.getMinecraft().getRenderViewEntity().rayTrace(200, 1.0F);
        if (pos != null) {
            EnumFacing blockHitSide = pos.sideHit;
            Block blockLookingAt = HubbyUtils.getClientWorld().getBlockState(pos.getBlockPos()).getBlock();
        }
        return null;
    }

    /**
     * Returns the block that the player is currently standing on
     */
    public static HubbyBlockResult getStandOnBlock() {
        return HubbyUtils.findBlockUnderEntity(HubbyUtils.getClientPlayer());
    }

    /**
     * Returns the first slot of the main inventory that possesses the item with the
     * specified class
     * @param itemClass - the class of the item to search for
     * @return int - the first found index (or -1 if not found)
     */
    public static List<Integer> getInventoryItemLocations(Class<? extends Item> itemClass) {
        List<Integer> indices = new ArrayList<Integer>();
        EntityPlayer player = HubbyUtils.getClientPlayer();
        if (player != null) {
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = player.inventory.mainInventory[i];
                if (stack != null && stack.getItem() != null && stack.getItem().getClass().isAssignableFrom(itemClass)) {
                    indices.add(i);
                }
            }
        }
        return indices;
    }

    /**
     * Returns the first item found in the inventory that matches the class passed in.
     * @param itemClass - the class of the <code>Item</code> we are searching for
     * @return ItemStack - the item or null if not found (returns a copy of the original
     * <code>ItemStacks</code> so that modification of the original list is permissible
     */
    public static Map<Integer, ItemStack> getInventoryItem(Class<? extends Item> itemClass) {
        EntityPlayer player = HubbyUtils.getClientPlayer();
        List<Integer> indices = HubbyUtils.getInventoryItemLocations(itemClass);
        Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
        for (int i : indices) {
            ItemStack copyStack = ItemStack.copyItemStack(player.inventory.mainInventory[i]);
            items.put(i, copyStack);
        }
        return items;
    }

    /**
     * Adds an item to the player's hotbar inventory
     * @param position - the pos where to set the item (should be [0,9))
     * @param item - the item to set
     * @param amount - the size of the stack
     */
    public static <T extends Item> void setInventoryHotBarItem(final Integer position, final T item, final Integer amount) {
        HubbyNetworkHelper.executeAndSendToServer(HubbyClientPacketType.PLAYER_INVENTORY, HubbyNetworkHelper.getDefaultChannelName(), new Callable() {
            public Map<String, Object> call() {
                assert position >= 0 && position < 9 : "Invalid hotbar position specified!";
                Integer slot = position + HubbyConstants.HOTBAR_INVENTORY_OFFSET;
                ItemStack stack = item != null ? new ItemStack(item, amount) : null;
                HubbyUtils.getClientPlayer().inventoryContainer.putStackInSlot(slot, stack);

                Map<String, Object> args = new HashMap<String, Object>();
                args.put("stack", stack);
                args.put("slot", slot);
                return args;
            }
        });
    }

    /**
     * Returns if the player is playing in creative mode
     * @return boolean - is creative mode
     */
    public static boolean isCreativeMode() {
        EntityPlayer player = HubbyUtils.getClientPlayer();
        if (player != null) {
            return player.capabilities.isCreativeMode;
        }
        return false;
    }

    /**
     * Returns the elapsed time for the difference between the 2 UTC timestamps.
     * Time value returned is in seconds.
     * @param utcStart - the starting timestamp
     * @param utcEnd - the ending timestamp
     * @return float - the elapsed time in seconds
     */
    public static float getElapsedTimeSeconds(Long utcStart, Long utcEnd) {

        // catch the invalid case
        if (utcStart >= utcEnd) {
            return 0.0000f;
        }

        // now we read the start time and calculate our elapsed time
        // for handling the packet that came down with the event
        Date startDate = new Date(utcStart);
        Date endDate = new Date(utcEnd);
        Long duration = endDate.getTime() - startDate.getTime();
        Long millsDiff = TimeUnit.MILLISECONDS.toMillis(duration);
        Long secsDiff = TimeUnit.MILLISECONDS.toSeconds(duration);
        Long minsDiff = TimeUnit.MILLISECONDS.toMinutes(duration);
        Long hoursDiff = TimeUnit.MILLISECONDS.toHours(duration);
        Long daysDiff = TimeUnit.MILLISECONDS.toDays(duration);

        // calc elapsed seconds and return
        float seconds = (float) secsDiff + (float) millsDiff / 1000.0f;
        seconds += (60.0f * minsDiff);
        seconds += (60.0f * 60.0f * hoursDiff);
        seconds += (60.0f * 60.0f * 24.0f * daysDiff);
        return seconds;
    }

    /**
     * Overwrites the value for the static final field that is identified
     * by class and name with the new value passed in
     * @param klass - the class containing the field
     * @param fieldName - the name of the field
     * @param newValue - the new value to set
     * @return boolean - were we successful or not?
     * @throws Exception
     */
    public static boolean overrideFinalFieldValue(Class klass, Object instance, String fieldName, Object newValue) {
        try {
            Field field;
            field = klass.getDeclaredField(fieldName);
            field.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(instance, newValue);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Enables/disables standard alpha blending
     * @param enable - turn on blending?
     */
    public static void enableStandardBlending(boolean enable) {
        if (enable) {
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
        else {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    /**
     * Checks if the specified item is in the player's inventory
     * @param item - the item to check
     * @return boolean - is item in inventory?
     */
    public static boolean isInventoryItem(Item item) {
        EntityPlayer player = HubbyUtils.getClientPlayer();
        List<Integer> indices = HubbyUtils.getInventoryItemLocations(item.getClass());
        return indices.size() > 0;
    }

    /**
     * Checks if the stack matches any of the stacks that are
     * in the player's current inventory
     * @param stack
     * @return
     */
    public static boolean isInventoryItem(ItemStack stack) {
        EntityPlayer player = HubbyUtils.getClientPlayer();
        List<Integer> indices = HubbyUtils.getInventoryItemLocations(stack.getItem().getClass());
        return indices.size() > 0;
    }

    /**
     * Is the item in the stack the item the player is currently using?
     * @param stack - the stack to check
     * @return boolean - is the player using us?
     */
    public static boolean isEquippedItem(Item item) {
        EntityPlayer player = HubbyUtils.getClientPlayer();
        if (player == null || player.inventory == null) {
            return false;
        }

        List<Integer> indices = HubbyUtils.getInventoryItemLocations(item.getClass());
        return indices.contains(player.inventory.currentItem);
    }

    /**
     * Checks if the item is specified is the currently
     * equipped item on the player
     * @param stack - the item to check
     * @return boolean - are the items equal?
     */
    public static boolean isEquippedItem(ItemStack stack) {
        EntityPlayer player = HubbyUtils.getClientPlayer();
        if (player == null || player.inventory == null) {
            return false;
        }

        for (int i = 0; i < HubbyConstants.HOTBAR_INVENTORY_SIZE; ++i) {
            ItemStack inventoryStack = player.inventory.mainInventory[i];
            if (ItemStack.areItemStacksEqual(inventoryStack, stack) && i == player.inventory.currentItem) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the index that represents the slot that the player
     * currently has selected as their active item
     * @return Integer - the slot (-1 for invalid cases)
     */
    public static Integer getPlayersCurrentlyEquippedItemSlot() {
        EntityPlayer player = HubbyUtils.getClientPlayer();
        if (player == null || player.inventory == null) {
            return -1;
        }
        return player.inventory.currentItem;
    }

    /**
     * Forward call, get an estimated delta time for the last frame
     * @return Long - the time in milliseconds
     */
    public static Long getDeltaTime() {
        return HubbyRefreshedObjectInterface.getDeltaTime();
    }

    /**
     * Forward call, get an estimated elapsed time for all frames
     * @return Long - the time in milliseconds
     */
    public static Long getElapsedTime() {
        return HubbyRefreshedObjectInterface.getElapsedTime();
    }

    /**
     * Returns the number of elapsed ticks
     * @return Integer - the number of ticks
     */
    public static Integer getElapsedTicks() {
        return HubbyRefreshedObjectInterface.getElapsedTicks();
    }

    /**
     * Returns the partial ticks for the elapsed time
     * (ie the progress towards the next whole tick)
     * @return Double - the partial ticks
     */
    public static Double getElapsedPartialTicks() {
        return HubbyRefreshedObjectInterface.getElapsedPartialTicks();
    }

    /**
     * Returns the delta time in ticks (most likely this will
     * always be 0, except in the case where a frame posted
     * a horrible update time).
     * @return
     */
    public static Integer getDeltaTicks() {
        return HubbyRefreshedObjectInterface.getDeltaTicks();
    }

    /**
     * Returns the number of partial ticks for the last delta time
     * @return Double - the partial ticks
     */
    public static Double getDeltaPartialtTicks() {
        return HubbyRefreshedObjectInterface.getDeltaPartialTicks();
    }

    /**
     * Collects all minecraft items that satisfy the predicate passed in
     * @param predicate - the predicate used to filter the list (can be null)
     * @return List - the list of items collected that satisfy the predicate
     */
    public static List<Item> collectAllItems(Predicate<Item> predicate) {
        ArrayList<Item> results = new ArrayList<Item>();
        Set<ResourceLocation> keys = Item.itemRegistry.getKeys();
        Iterator<ResourceLocation> it = keys.iterator();
        while (it.hasNext()) {
            ResourceLocation key = it.next();
            Object item = Item.itemRegistry.getObject(key);
            if (item != null && Item.class.isInstance(item)) {
                results.add((Item) item);
            }
        }

        // Filter the results if we have a valid predicate
        if (predicate != null) {
            Iterable<Item> iter = Iterables.filter(results, predicate);
            return Lists.newArrayList(iter.iterator());
        }
        return results;

        // An alternate way to do the same thing...
        //
        // ArrayList<Item> results = new ArrayList<Item>();
        // List<RegistryNamespaced> allRegistries = HubbyUtils.searchForFieldsOfType("net.minecraft.item", Item.class, null, RegistryNamespaced.class);
        // RegistryNamespaced registry = (RegistryNamespaced) allRegistries.get(0);
        // Set keys = registry.getKeys();
        //
        // // Iterate over all of the item keys, looking for any
        // // items that match the target class
        // for (Object key : keys) {
        // Item item = (Item) registry.getObject(key);
        // if (klass.isInstance(item)) {
        // results.add(item);
        // }
        // }
        //
        // // return the compiled list of items matching the class type
        // // that was passed into this method
        // return results;
    }

    /**
     * Returns the package name converted to a path name
     * @param packageName - the package name to convert
     * @return String - the path name
     */
    public static String convertPackageToPath(String packageName) {
        return packageName.replace('.', '/');
    }

    /**
     * Converts the path name to a package name
     * @param pathName - the name to convert
     * @return String - the converted package name
     */
    public static String convertPathToPackage(String pathName) {
        return pathName.replace('/', '.');
    }

    /**
     * This method returns a list containing all of the custom mod
     * items that have been created.
     * @return List - the list of items
     */
    public static <T extends Item> List<Item> collectItemsOfType(final Class<T> klass) {
        return HubbyUtils.collectAllItems(new Predicate<Item>() {
            @Override
            public boolean apply(Item item) {
                return klass.isInstance(item);
            }
        });
    }

    /**
     * Returns the string dimensions for the string passed in,
     * measuring both the width and the height of the string
     * @param source - the string to measure
     * @param spacing - the additional space to add to the calculated size
     * @return HubbySize - the size to return
     */
    public static HubbySize<Integer> getStringDimensions(String source, Integer spacing) {
        FontRenderer fontRender = Minecraft.getMinecraft().fontRendererObj;
        Integer lines = getStringLineCount(source);
        Integer width = 0;
        Integer height = fontRender.FONT_HEIGHT * lines + (spacing * 2);
        for (Integer i = 0; i < lines; ++i) {
            String lineText = getStringLine(source, i);
            Integer lineWidth = fontRender.getStringWidth(lineText) + (spacing * 2);
            width = lineWidth >= width ? lineWidth : width;
        }
        return new HubbySize<Integer>(width, height);
    }

    /**
     * Reads a line of text from the source string. If the line
     * is invalid then <code>null</code> is returned as we want
     * to distinguish ourselves from the empty string since that
     * could actually be a valid result based on user input
     * @param source - the source string
     * @param line - the line of text to get
     * @return String - the discovered line of text
     */
    public static String getStringLine(String source, Integer line) {
        Integer lineCount = getStringLineCount(source);
        if (line >= lineCount || line < 0) {
            return "";
        }

        // return the source as it is since there is
        // only one line anyway
        if (lineCount == 1) {
            return source;
        }

        // if we have more than one line but we only want the
        // first line then we can do an easy calculation here
        // to do just that
        if (line == 0) {
            return source.substring(0, source.indexOf('\n'));
        }

        // here we iterate looking for the newline
        // character so that we count the number of
        // lines traversed until we get to the line
        // that we care about
        Integer startIndex = 0;
        Integer index = 0;
        while (line > 0) {
            startIndex = index;
            index = source.indexOf('\n', index) + 1;
            if (index == 0) {
                return "";
            }
            --line;
        }

        // Returns the substring based on indices we
        // calculated when determining which line of
        // text to gather
        return source.substring(startIndex, index - 1);
    }

    /**
     * Returns the number of lines to be considered in the source text provided
     * @param source - the source string to calculate the line count for
     * @return Integer - the number of lines found
     */
    public static Integer getStringLineCount(String source) {
        Integer lines = source.length() > 0 ? 1 : 0;
        Integer index = 0;

        // iterate over entire string to determine how many lines
        // of text we have. Note, we only consider the text to have
        // an additional line if the current index is less than the
        // length of the string (ie. if you have a newline character
        // as your last character in the string, that will not count
        // as another line of text.
        while (index < source.length()) {
            if (source.charAt(index) == HubbyConstants.NEWLINE_CHARACTER && index < source.length() - 1) {
                ++lines;
            }
            ++index;
        }

        // return our calculated lines
        return lines;
    }

    /**
     * Returns a list of all of the lines of text that
     * exist within the source string passed in
     * @param source - the source string to get lines
     * @param width - the max line width (-1 is infinite)
     * @return
     */
    public static List<String> getStringLinesForWidth(String source, Integer width) {
        FontRenderer fontRender = Minecraft.getMinecraft().fontRendererObj;
        return fontRender.listFormattedStringToWidth(source, width < 0 ? 100000 : width);
    }

    /**
     * This is a formatted method that guarantees to return a string that
     * will fit in the width (in pixels) specified. If during the parsing
     * a valid space character could not be found to divide the string, then
     * the parsed word will be cut in two, using a hyphen to denote it is divided.
     * @param source - the source string to fit
     * @param maxWidth - the maximum allowed width in pixels to fit the string in
     * @return String - the formatted string that fits the width provided
     */
    public static String getStringForWidth(String source, int maxWidth) {
        return getStringForWidth(source, maxWidth, false);
    }

    /**
     * Joins all of the strings in the list together by using the separator value as the glue
     * @param strings - the list of strings to join
     * @param separator - the string to link the strings together
     * @return String - the combined string containing all of the strings passed in
     */
    public static String joinStrings(List<String> strings, String separator) {
        String builder = "";
        for (Integer i = 0; i < strings.size(); ++i) {
            builder += strings.get(i);
            if (i < strings.size() - 1) {
                builder += separator;
            }
        }
        return builder;
    }

    /**
     * This is a formatted method that guarantees to return a string that
     * will fit in the width (in pixels) specified. If during the parsing
     * a valid space character could not be found to divide the string, then
     * the parsed word will be cut in two, using a hyphen to denote it is divided.
     * @param source - the source string to fit
     * @param maxWidth - the maximum allowed width in pixels to fit the string in
     * @param useMcMethod - should we use the provided minecraft method for this operation?
     * @return String - the formatted string that fits the width provided
     */
    public static String getStringForWidth(String source, int maxWidth, boolean useMcMethod) {

        FontRenderer fontRender = Minecraft.getMinecraft().fontRendererObj;

        // here we use the method provided by mc for breaking the
        // string into pieces where each string fits within the desired width
        if (useMcMethod) {
            String formatted = "";
            List<String> list = getStringLinesForWidth(source, maxWidth);
            for (int i = 0; i < list.size(); ++i) {
                formatted += list.get(i);
                if (i < list.size() - 1) {
                    formatted += "\n";
                }
            }
            return formatted;
        }

        // otherwise, we use this code to achieve the same thing
        Integer currentIndex = 0;
        Integer lastSpaceIndex = -1;
        Integer originalLastSpaceIndex = -1;
        String workingString = "";
        String fittedString = "";

        // Check the string width for the original string passed in, and if
        // it is less than the max width we are good to go and can return
        // the original as is
        Integer currentWidth = fontRender.getStringWidth(source);
        if (currentWidth <= maxWidth) {
            return source;
        }

        // otherwise, we want to parse the original string and insert
        // newline characters into any spaces we when the parsing the string.
        // If no valid space could be found then we simply break the word
        // with a hyphen and continue on.

        // keep looping until we have built a working string that is as long
        // as it possibly can be without violating the max width requirement.
        // Once we have that substring we then append it to our fitted string
        // as being text that has been parsed and fitted within the max width
        // constraint.
        Integer workingWidth = fontRender.getStringWidth(workingString);
        while (currentIndex < source.length()) {

            // get the current character at the specified index
            // and note whether or not we have encountered a space
            // character
            char character = source.charAt(currentIndex);
            if (character == ' ') {
                originalLastSpaceIndex = currentIndex;
                lastSpaceIndex = workingString.length();
            }

            // append the character and check our current width;
            // if we fit then we will continue the iteration and move
            // on to the next character.
            workingString += character;
            workingWidth = fontRender.getStringWidth(workingString);

            // our working string is too big, we've added one too many
            // characters and need to adjust.
            if (workingWidth > maxWidth) {

                // if we have found a space char during our iteration then
                // we will replace that with a newline and then adjust the current
                // index to be the position of the replaced space character plus one.
                if (lastSpaceIndex >= 0) {
                    workingString = workingString.substring(0, lastSpaceIndex);
                    workingString += "\n";
                    currentIndex = originalLastSpaceIndex + 1;
                    lastSpaceIndex = -1;
                }
                // if we don't have a space then we back up one character and
                // insert a hyphen to break the word into two followed by a newline
                // to make the working string fit within the bounds. Note, we also
                // do not adjust the current index as we want to re-process that
                // character since it did not fit.
                else {
                    workingString = workingString.substring(0, workingString.length() - 1);
                    workingString += "-\n";
                }

                // append our working string to the fitted string
                fittedString += workingString;
                workingString = "";
            }
            // If we get here then we want to increment the next character.
            // If we have reached the end of the original string then append
            // the working string since we know it fits and its the last part
            // of the original string that we just parsed.
            else {
                ++currentIndex;
            }
        }

        // append the remaining of the working string to complete the fitting
        fittedString += workingString;
        return fittedString;
    }

    /**
     * Closes the current gui screen if one is currently open
     * @return boolean - was a screen closed
     */
    public static boolean closeCurrentScreen() {
        if (Minecraft.getMinecraft().currentScreen != null) {
            Minecraft.getMinecraft().currentScreen = null;
            return true;
        }
        return false;
    }

    /**
     * Closes the current screen only if it matches the class passed in
     * @param klass - the <code>GuiScreen</code> class to check for
     * @return boolean - did we close the screen?
     */
    public static <T extends GuiScreen> boolean closeCurrentScreen(Class<T> klass) {
        if (Minecraft.getMinecraft().currentScreen != null && klass.isInstance(Minecraft.getMinecraft().currentScreen)) {
            Minecraft.getMinecraft().currentScreen = null;
            return true;
        }
        return false;
    }
    
    /**
     * Returns the substring that occurs after the match string
     * @param source - the source string
     * @param match - the string to match
     * @return String - the substring that begins after the place where the match was found
     */
    public static String substringAfter(String source, String match) {
        return HubbyUtils.substringAfter(source, match, -1);
    }

    /**
     * Returns the substring that occurs after the match string
     * @param source - the source string
     * @param match - the string to match
     * @param offset - the offset to move after the match location
     * @return String - the substring that begins after the place where the match was found
     */
    public static String substringAfter(String source, String match, Integer offset) {
        Integer index = source.indexOf(match);
        offset = match.length() + offset;
        if (index + offset < source.length()) {
            return source.substring(index + offset);
        }
        return "";
    }

    /**
     * Returns the substring by removing all occurrences of a specific string from its self
     * @param source - the source string
     * @param toRemove - the string to remove
     * @return String - the substring
     */
    public static String substringRemoveAll(String source, String toRemove) {
        Integer index = source.indexOf(toRemove);
        if (index < 0) {
            return source;
        }
        String newString = source.substring(0, index) + source.substring(index + toRemove.length(), source.length());
        return substringRemoveAll(newString, toRemove);
    }
    
    /**
     * Returns the substring by removing an occurrence of a specific string from its self
     * @param source - the source string
     * @param toRemove - the string to remove
     * @param startIndex - the index to start the search at
     * @return String - the substring
     */
    public static String substringRemove(String source, String toRemove, Integer startIndex) {
        Integer index = source.indexOf(toRemove, startIndex);
        if (index < 0) {
            return source;
        }
        return source.substring(0, index) + source.substring(index + toRemove.length(), source.length());
    }
    
    /**
     * Returns the substring by removing an occurrence of a specific string from its self
     * @param source - the source string
     * @param toRemove - the string to remove
     * @return String - the substring
     */
    public static String substringRemove(String source, String toRemove) {
        return substringRemove(source, toRemove, 0);
    }

    /**
     * Gets the default resource for a block or an item
     * @param blockNotItem - are we getting the resource for a block
     * @return ResourceLocation - the resource
     */
    public static HubbyResourceLocation getDefaultResource(boolean blockNotItem) {
        if (blockNotItem) {
            return new HubbyResourceLocation(HubbyConstants.MINECRAFT_MOD_ID, DEFAULT_BLOCK_RESOURCE);
        }
        else {
            return new HubbyResourceLocation(HubbyConstants.MINECRAFT_MOD_ID, DEFAULT_ITEM_RESOURCE);
        }
    }

    /**
     * Attempt to find the resource location for the block passed in
     * @param blockResult - the block to search for the resource
     * @return ResourceLocation - the resource location of the block (default block resource if not found)
     */
    public static HubbyResourceLocation getBlockResourceLocation(HubbyBlockResult blockResult) {
        try {
            ResourceLocation blockResource = null;
            Set keys = Block.blockRegistry.getKeys();
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                HubbyResourceLocation rl = new HubbyResourceLocation(((ResourceLocation) it.next()));
                Block registeredBlock = (Block) Block.blockRegistry.getObject(rl);
                if (blockResult.getBlock() == registeredBlock) {
                    return getResourceLocation(blockResult, rl);
                }
            }
        }
        catch (Exception e) {
        }
        return getDefaultResource(true);
    }

    /**
     * Attempts to find the resource location for the item passed in
     * @param item - the item to search for the resource
     * @return ResourceLocation - the resource location of the item (default item resource if not found)
     */
    public static HubbyResourceLocation getItemResourceLocation(Item item) {
        try {
            ResourceLocation itemResource = null;
            Set keys = Item.itemRegistry.getKeys();
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                HubbyResourceLocation rl = new HubbyResourceLocation(((ResourceLocation) it.next()));
                Item registeredItem = (Item) Item.itemRegistry.getObject(rl);
                if (item == registeredItem) {
                    return getResourceLocation(item, rl);
                }
            }
        }
        catch (Exception e) {
        }
        return getDefaultResource(false);
    }
    
    /**
     * Attempts to lookup an item from the item registry based on name
     * @param name - the name (or partial name) of the item to lookup
     * @return Item - the found item (null if no matches were found)
     */
    public static Item searchForItem(String name) {
        name = name.toLowerCase();
        Set keys = Item.itemRegistry.getKeys();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            ResourceLocation rl = (ResourceLocation) it.next();
            Item item = (Item) Item.itemRegistry.getObject(rl);
            if (item.getUnlocalizedName().toLowerCase().contains(name) || rl.getResourcePath().toLowerCase().contains(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns the resource location for the item passed in
     * @param item - the <code>Item</code> to get the <code>ResourceLocation</code> for
     * @param blockResult - the result to use if we are rendering a block with multiple states
     * @return ResourceLocation - the items texture location (null if it could not be determined)
     * @throws IOException 
     */
    private static HubbyResourceLocation getResourceLocation(Object obj, HubbyResourceLocation baseResource) throws IOException {

        // make sure that we are working with a valid resource location...
        // any blocks that have variants need an updated resource location
        // that points to the variant on disk... for example, if the player
        // is standing on a 'wool' block, the resource will simply be pointing
        // to 'wool.json' which does not exist, rather we need it to be
        // of the form 'blue_wool.json' which is what this method call does
        HubbyBlockResult blockResult = HubbyBlockResult.class.isInstance(obj) ? (HubbyBlockResult) obj : null;
        Item itemResult = Item.class.isInstance(obj) ? (Item) obj : null;
        boolean isItemModel = itemResult != null;
        HubbyResourceLocation finalResource = blockResult != null ? getVariantResourceForBlock(baseResource, blockResult) : baseResource;
        HubbyResourceLocation jsonResource = null;
        String modID = finalResource != null ? finalResource.getResourceDomain() : HubbyConstants.MINECRAFT_MOD_ID;

        // check to make sure that we have valid input, if not, then
        // we want to return the default resource to be safe
        if ((itemResult == null && blockResult == null) || finalResource == null) {
            return getDefaultResource(false);
        }
        
        // if the item matches then we build the name for the json location and we
        // attempt to parse the json trying to the resource name for the
        // corresponding texture for the item we just matched.
        jsonResource = new HubbyResourceLocation(HubbyUtils.getResourceLocation(modID, "models/item/" + finalResource.getResourcePath() + ".json"));
        jsonResource.setMetadata(finalResource.getMetadata(null));
        if (!Minecraft.getMinecraft().mcDefaultResourcePack.resourceExists(jsonResource)) {
            jsonResource = new HubbyResourceLocation(HubbyUtils.getResourceLocation(modID, "models/block/" + finalResource.getResourcePath() + ".json"));
            jsonResource.setMetadata(finalResource.getMetadata(null));
            isItemModel = false;

            // now we check the block resource, and if we can't find any then we return the default resource
            if (!Minecraft.getMinecraft().mcDefaultResourcePack.resourceExists(jsonResource)) {
                Block itemBlock = Block.getBlockFromItem(itemResult);
                return getDefaultResource(itemResult != null);
            }
        }

        // get the parent resource if we have one and we don't have a "textures" field
        jsonResource = getParentResource(jsonResource, true);
        if (jsonResource.isTexture()) {
            return jsonResource;
        }

        // attempt to read the json resource, if it does not exist then we return the default
        if (!Minecraft.getMinecraft().mcDefaultResourcePack.resourceExists(jsonResource)) {
            return getDefaultResource(!isItemModel);
        }

        // get the json root and lets lookup the texture field
        InputStream stream = Minecraft.getMinecraft().mcDefaultResourcePack.getInputStream(jsonResource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charsets.UTF_8));
        JsonObject jsonRoot = (new JsonParser()).parse(reader).getAsJsonObject();

        // look for the "textures" field (which at this point we should have a guarantee that the
        // json object does in fact have a "textures" field
        if (JsonUtils.jsonObjectHasNamedField(jsonRoot, "textures")) {

            // get the texture object and begin checking for all of these valid entries, the entries
            // occur in order that selects the best option first in terms of finding a texture to represent
            // the item or block that was passed in
            JsonObject texturesObj = JsonUtils.getJsonObject(jsonRoot, "textures");
            String[] fieldNames = new String[] { "layer0", "side", "all", "wool", "plant", "cactus", "cross", "north", "east", "south", "west" };
            for (String field : fieldNames) {
                if (JsonUtils.jsonObjectHasNamedField(texturesObj, field)) {
                    String textureName = JsonUtils.getJsonObjectStringFieldValue(texturesObj, field);
                    Integer index = textureName.indexOf(":");
                    textureName = textureName.substring(0, index + 1) + "textures/" + textureName.substring(index + 1, textureName.length()) + ".png";
                    return new HubbyResourceLocation(textureName);
                }
            }

            // if we are an item then there is nothing else we can do... let's bail and return the default
            if (blockResult == null || isItemModel) {
                return getDefaultResource(false);
            }

            // if we get here then that mean all of texture fields we checked for
            // were not found, so, we will just move down to the next block until
            // we find a block that we can use for the resource location
            if (JsonUtils.jsonObjectHasNamedField(texturesObj, "particle")) {
                HubbyBlockResult nextResult = new HubbyBlockResult();
                while (!nextResult.isValid()) {
                    nextResult = HubbyUtils.getNextBlock(blockResult, new BlockPos(0, -1, 0), false);
                }
                return getBlockResourceLocation(nextResult);
            }
        }

        // if we get here then we have totally failed and
        // we could not find anything in terms of a resource
        // location that matches the obj passed in
        return getDefaultResource(!isItemModel);
    }

    /**
     * Returns the parent resource for the resource passed in
     * @param resource - the base resource to get the parent of
     * @param noTextureOnly - only get the parent resource if there is no texture field
     * @param type - a simple identifier for various resources
     * @return ResourceLocation - the parent resource location (or the resource passed in if we failed)
     */
    private static HubbyResourceLocation getParentResource(HubbyResourceLocation resource, boolean noTextureOnly) {
        try {
            String modID = resource.getResourceDomain();
            InputStream stream = Minecraft.getMinecraft().mcDefaultResourcePack.getInputStream(resource);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charsets.UTF_8));
            JsonObject json = (new JsonParser()).parse(reader).getAsJsonObject();

            // make sure we allow the texture field or that the texture field is absent if we
            // don't allow it when determining the parent resource
            if ((!noTextureOnly || !JsonUtils.jsonObjectHasNamedField(json, "textures")) && JsonUtils.jsonObjectHasNamedField(json, "parent")) {
                String parentName = JsonUtils.getJsonObjectStringFieldValue(json, "parent");
                
                // check if we are the built-in type? If we are, then we need to do a little more
                // processing to get to the resource path that we want
                if (parentName.contains(HubbyConstants.RESOURCE_BUILTIN_KEY)) {
                    String builtinName = substringAfter(parentName, HubbyConstants.RESOURCE_BUILTIN_KEY, 1); 
                    String modelName = resource.getModelName(false);
                    return getBuiltinResource(resource, builtinName, modelName);
                }

                // this is the default case... most likely and item -> block or vice versa
                return new HubbyResourceLocation(modID, "models/" + parentName + ".json", resource.getMetadata(null));
            }
        }
        catch (Exception e) {
        }

        // we return the default if we could not find a valid parent
        return resource;
    }
    
    /**
     * Based on the passed in resource, builds the resource location for the builtin identified by name and model
     * @param resource - the base resource
     * @param builtin - the name of the builtin type
     * @param model - the model for the builtin
     * @return HubbyResourceLocation - the builtin resource
     */
    private static HubbyResourceLocation getBuiltinResource(HubbyResourceLocation resource, String builtin, String model) {
        String modID = resource.getResourceDomain();
        
        // Are we an ender chest?
        if (ChestType.getChestResource(resource) == ChestType.ENDER) {
            ChestType ct = ChestType.getChestResource(resource);
            return new HubbyResourceLocation(modID, "textures/" + builtin + "/" + model + "/" + ct.getChestBaseTextureName() + ".png", resource.getMetadata(null));
        }
        // Are we a chest at all?
        else if (ChestType.getChestResource(resource) != ChestType.INVALID) {
            ChestType ct = ChestType.getChestResource(resource);
            return new HubbyResourceLocation(modID, "textures/" + builtin + "/" + model + "/" + ct.getChestBaseTextureName() + ".png", resource.getMetadata(null));
        }
        
        // not a valid builtin that we could determine
        return null;
    }

    /**
     * Returns the adjusted resource location for the block result passed in.
     * If there is no determined variant then this function returns the resource
     * location passed in unaffected.
     * @param rl - the initial resource location
     * @param blockResult - the block result to determine resource for
     * @return ResourceLocation - the adjusted location (returns default location if the block result has no variant).
     */
    private static HubbyResourceLocation getVariantResourceForBlock(HubbyResourceLocation rl, HubbyBlockResult blockResult) {

        // return the default location if we are invalid
        if (rl == null || blockResult == null || !blockResult.isValid()) {
            return rl;
        }

        // check for colored blocks and if we have one, then we want to update
        // the resource location so that it refers to the colored version of the block
        // and not the base block which points to an invalid resource location
        if (blockResult != null && BlockStainedGlass.class.isInstance(blockResult.getBlock())) {
            EnumDyeColor variant = (EnumDyeColor) blockResult.getBlockState().getValue(BlockColored.COLOR);
            rl = new HubbyResourceLocation(rl.getResourceDomain(), variant.getName() + "_" + rl.getResourcePath());
        }
        else if (blockResult != null && BlockChest.class.isInstance(blockResult.getBlock())) {
            int type = ((BlockChest)blockResult.getBlock()).chestType;
            rl = new HubbyResourceLocation(rl.getResourceDomain(), "chest");
            rl.setMetadata(ChestType.getEnumForValue(type));
        }
        else if (blockResult != null && BlockEnderChest.class.isInstance(blockResult.getBlock())) {
            rl = new HubbyResourceLocation(rl.getResourceDomain(), "chest");
            rl.setMetadata(ChestType.ENDER);
        }
        else if (blockResult != null && BlockDoubleWoodSlab.class.isInstance(blockResult.getBlock())) {
            BlockPlanks.EnumType variant = (BlockPlanks.EnumType) blockResult.getBlockState().getValue(BlockWoodSlab.VARIANT);
            rl = new HubbyResourceLocation(rl.getResourceDomain(), variant.getName() + "_slab");
        }
        else if (blockResult != null && BlockCarpet.class.isInstance(blockResult.getBlock())) {
            EnumDyeColor variant = (EnumDyeColor) blockResult.getBlockState().getValue(BlockCarpet.COLOR);
            rl = new HubbyResourceLocation(rl.getResourceDomain(), rl.getResourcePath() + "_" + variant.getName());
        }
        else if (blockResult != null && BlockColored.class.isInstance(blockResult.getBlock())) {
            EnumDyeColor variant = (EnumDyeColor) blockResult.getBlockState().getValue(BlockColored.COLOR);
            rl = new HubbyResourceLocation(rl.getResourceDomain(), variant.getName() + "_" + rl.getResourcePath());
        }
        else if (blockResult != null && BlockWoodSlab.class.isInstance(blockResult.getBlock())) {
            BlockPlanks.EnumType variant = (BlockPlanks.EnumType) blockResult.getBlockState().getValue(BlockWoodSlab.VARIANT);
            rl = new HubbyResourceLocation(rl.getResourceDomain(), variant.getName() + "_slab");
        }
        else if (blockResult != null && BlockStoneSlab.class.isInstance(blockResult.getBlock())) {
            BlockStoneSlab.EnumType variant = (BlockStoneSlab.EnumType) blockResult.getBlockState().getValue(BlockStoneSlab.VARIANT);
            rl = new HubbyResourceLocation(rl.getResourceDomain(), variant.getName() + "_slab");
        }
        else if (blockResult != null && BlockPlanks.class.isInstance(blockResult.getBlock())) {
            BlockPlanks.EnumType variant = (BlockPlanks.EnumType) blockResult.getBlockState().getValue(BlockPlanks.VARIANT);
            rl = new HubbyResourceLocation(rl.getResourceDomain(), variant.getName() + "_" + rl.getResourcePath());
        }
        else if (blockResult != null && BlockSapling.class.isInstance(blockResult.getBlock())) {
            BlockPlanks.EnumType variant = (BlockPlanks.EnumType) blockResult.getBlockState().getValue(BlockSapling.TYPE);
            rl = new HubbyResourceLocation(rl.getResourceDomain(), variant.getName() + "_" + rl.getResourcePath());
        }
        else if (blockResult != null && BlockOldLeaf.class.isInstance(blockResult.getBlock())) {
            BlockPlanks.EnumType variant = (BlockPlanks.EnumType) blockResult.getBlockState().getValue(BlockOldLeaf.VARIANT);
            rl = new HubbyResourceLocation(rl.getResourceDomain(), variant.getName() + "_" + rl.getResourcePath());
        }
        else if (blockResult != null && BlockOldLog.class.isInstance(blockResult.getBlock())) {
            BlockPlanks.EnumType variant = (BlockPlanks.EnumType) blockResult.getBlockState().getValue(BlockOldLog.VARIANT);
            rl = new HubbyResourceLocation(rl.getResourceDomain(), variant.getName() + "_" + rl.getResourcePath());
        }
        else if (blockResult != null && BlockNewLeaf.class.isInstance(blockResult.getBlock())) {
            BlockPlanks.EnumType variant = (BlockPlanks.EnumType) blockResult.getBlockState().getValue(BlockNewLeaf.VARIANT);
            String path = rl.getResourcePath();
            Character ch = rl.getResourcePath().charAt(rl.getResourcePath().length() - 1);
            if (Character.isDigit(ch)) {
                path = rl.getResourcePath().substring(0, rl.getResourcePath().length() - 1);
            }
            rl = new HubbyResourceLocation(rl.getResourceDomain(), variant.getName() + "_" + path);
        }
        else if (blockResult != null && BlockNewLog.class.isInstance(blockResult.getBlock())) {
            BlockPlanks.EnumType variant = (BlockPlanks.EnumType) blockResult.getBlockState().getValue(BlockNewLog.VARIANT);
            rl = new HubbyResourceLocation(rl.getResourceDomain(), variant.getName() + "_" + rl.getResourcePath());
        }
        else if (blockResult != null && BlockFlowerPot.class.isInstance(blockResult.getBlock())) {
            BlockFlowerPot.EnumFlowerType variant = (BlockFlowerPot.EnumFlowerType) blockResult.getBlockState().getValue(BlockFlowerPot.CONTENTS);
            rl = new HubbyResourceLocation(rl.getResourceDomain(), rl.getResourcePath() + "_" + variant.getName());
        }
        else if (blockResult != null && BlockFlower.class.isInstance(blockResult.getBlock())) {
            BlockFlower.EnumFlowerType variant = (BlockFlower.EnumFlowerType) blockResult.getBlockState().getValue(((BlockFlower) blockResult.getBlock()).getTypeProperty());
            rl = new HubbyResourceLocation(rl.getResourceDomain(), variant.name().toLowerCase());
        }
        else if (blockResult != null && BlockStone.class.isInstance(blockResult.getBlock())) {
            BlockStone.EnumType variant = (BlockStone.EnumType) blockResult.getBlockState().getValue(BlockStone.VARIANT);
            String[] parts = variant.getName().split("_");
            if (parts.length > 1) {
                rl = new HubbyResourceLocation(rl.getResourceDomain(), parts[1] + "_" + parts[0]);
            }
            else if (!rl.getResourcePath().equals(variant.getName())) {
                rl = new HubbyResourceLocation(rl.getResourceDomain(), rl.getResourcePath() + "_" + variant.getName());
            }
            else {
                rl = new HubbyResourceLocation(rl.getResourceDomain(), rl.getResourcePath());
            }
        }

        return rl;
    }
}
