package com.hubby.shared.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * This class stores a number of static methods that perform
 * useful tasks making life a little bit easier
 * @author davidleistiko
 */
public class HubbyUtils {
	
	/**
	 * Enumerate the options for the draw gradient method
	 * @author davidleistiko
	 */
	public enum GradientMode {
		HORIZONTAL,
		VERTICAL
	}
	
	/**
	 * Identifies that describe the type of rendering we are 
	 * registering a particular item for
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
	 * Registers a 'KeyBinding' which we can use to determine if
	 * the user is currently pressing any button
	 * @param alias - the id name for the key
	 * @param binding - the actual binding
	 */
	public static void regiterKeyBinding(String alias, KeyBinding binding) {
	    ClientRegistry.registerKeyBinding(binding);
		KEY_BINDINGS.put(alias, binding);
	}
	
	/**
	 * Returns a list of all pressed keys currently held
	 * down by the user
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
	 * Returns the isPressed result for the key that
	 * is identified by name
	 * @param alias
	 * @return
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
     * Helper method that will draw a rectangle filled with a gradient color scheme
     * using the minecraft built-in tessellator. Gradient can be either horizontal or vertical
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
     * Check if a value is within the range provided
     * @param value - the value to check
     * @param min - the least most possible value
     * @param max - the max most possible value
     * @return boolean - whether the value was within the range or not
     */
    public static boolean isWithinRange(int value, int min, int max) {
    	return value >= min && value <= max;
    }
    
    /**
     * Check if a value is within the range provided
     * @param value - the value to check
     * @param min - the least most possible value
     * @param max - the max most possible value
     * @return boolean - whether the value was within the range or not
     */
    public static boolean isWithinRange(float value, float min, float max) {
    	return value >= min && value <= max;
    }
    
    /**
     * Helper function that adds liquid that was scooped up
     * into the bucket that scooped it
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
        //if (block instanceof NitroBlockFluid && block.getMaterial() == NitroInterface.nitroFluidMaterial) {
		if (true) {
			bucket = null; //NitroInterface.nitroBucketFluid;
		} else if (block == Blocks.lava || block == Blocks.flowing_lava) {
			bucket = (ItemBucket) Items.lava_bucket;
		} else if (block == Blocks.water || block == Blocks.flowing_water) {
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
     * Returns a pseudo-randomly generated color value with a specific alpha
     * @param alpha - the value for the alpha channel
     * @return Color - the random color
     */
	public static HubbyColor getRandomColor(float alpha) {
		final Random random = new Random();
		float red = random.nextFloat() % 256.0f;
		float green = random.nextFloat() % 256.0f;
		float blue = random.nextFloat() % 256.0f;
		return new HubbyColor(red, green, blue, alpha);
	}
	
	/**
	 * Clamps the value between the min and max values
	 * @param value - the value to clamp
	 * @param min - the min allowed value
	 * @param max - the max allowed value
	 * @return int - the clamped value
	 */
	public static int clamp(int value, int min, int max) {
		return Math.min(max, Math.max(value, min));
	}
	
	/**
	 * Clamps the value between the min and max values
	 * @param value - the value to clamp
	 * @param min - the min allowed value
	 * @param max - the max allowed value
	 * @return float - the clamped value
	 */
	public static float clamp(float value, float min, float max) {
		return Math.min(max, Math.max(value, min));
	}

	/**
	 * Clamps the value between the min and max values
	 * @param value - the value to clamp
	 * @param min - the min allowed value
	 * @param max - the max allowed value
	 * @return double - the clamped value
	 */
	public static float clamp(double value, double min, double max) {
		return Math.min((float)max, Math.max((float)value, (float)min));
	}
}
