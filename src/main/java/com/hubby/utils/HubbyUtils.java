package com.hubby.utils;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.lwjgl.opengl.GL11;
import org.reflections.Reflections;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.hubby.utils.HubbyConstants.ArmorType;
import com.hubby.utils.HubbyConstants.Direction;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
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
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
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

    /**
     * Stores all registered key-bindings and uses these to detect when the user
     * has pressed a key
     */
    public static final HashMap<String, KeyBinding> KEY_BINDINGS = new HashMap<String, KeyBinding>(32);

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
     */
    public static void drawTexturedRectHelper(float zLevel, int posX, int posY, int width, int height, int u1, int v1, int u2, int v2) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().addVertexWithUV((double) (posX + 0), (double) (posY + height), (double) zLevel, (double) ((float) (u1) * f), (double) ((float) (v2) * f1));
        tessellator.getWorldRenderer().addVertexWithUV((double) (posX + width), (double) (posY + height), (double) zLevel, (double) ((float) (u2) * f), (double) ((float) (v2) * f1));
        tessellator.getWorldRenderer().addVertexWithUV((double) (posX + width), (double) (posY + 0), (double) zLevel, (double) ((float) (u2) * f), (double) ((float) (v1) * f1));
        tessellator.getWorldRenderer().addVertexWithUV((double) (posX + 0), (double) (posY + 0), (double) zLevel, (double) ((float) (u1) * f), (double) ((float) (v1) * f1));
        tessellator.draw();
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
    public static boolean isClienSide() {
        return !HubbyUtils.isServerSide();
    }
    
    /**
     * Check if we are on the client based on the world passed in
     * @param world - The <code>World</code> to check
     * @return boolean - true if we are the client
     */
    public static boolean isClientSide(World world) {
        return !world.isRemote;
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
    public static long getTimeUTC() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        int utcOffset = c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET);
        return c.getTimeInMillis() + utcOffset;
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

        // iterate over all fields and add any Materials that we find
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

        // Find the itemRegistry that is a map containing all of the generic Minecraft items
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
    public static Block findBlockUnderEntity(Entity entity) {
        int blockX = MathHelper.floor_double(entity.posX);
        int blockY = MathHelper.floor_double(entity.getEntityBoundingBox().minY) - 1;
        int blockZ = MathHelper.floor_double(entity.posZ);
        BlockPos pos = new BlockPos(blockX, blockY, blockZ);
        Block block = entity.worldObj.getBlockState(pos).getBlock();
        Item blockItem = Item.getItemFromBlock(block);
        
        // search for the first non-air block below us if indeed
        // our current underneath block is air
        int yOffset = -1;
        while (blockItem == null && yOffset > (int)-pos.getY()) {
            BlockPos offsetPos = pos.add(0, yOffset, 0.0f);
            block = HubbyUtils.getServerWorld().getBlockState(offsetPos).getBlock();
            if (block != null) {
                blockItem = Item.getItemFromBlock(block);
            }
            yOffset -= 1;
        }
        
        return block;
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
}
