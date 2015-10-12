package com.hubby.ultra.setup;

import com.hubby.ultra.UltraConstants;
import com.hubby.ultra.UltraConstants.BackpackType;
import com.hubby.ultra.UltraLightHelper;
import com.hubby.ultra.gui.UltraGuiScreenTeleportWaypoint;
import com.hubby.ultra.items.UltraItemAdvancedArmor;
import com.hubby.ultra.items.UltraItemBackpack;
import com.hubby.ultra.items.UltraItemBasicSword;
import com.hubby.ultra.items.UltraItemGlowStick;
import com.hubby.ultra.items.UltraItemTeleportArtifact;
import com.hubby.utils.HubbyConstants.ArmorType;
import com.hubby.utils.HubbyLimitedInventoryItem;
import com.hubby.utils.HubbyUtils;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;

import net.minecraftforge.common.util.EnumHelper;

/**
 * This class stores the references to all blocks/items/recipes/etc for the mod
 * @author davidleistiko
 */
public class UltraRegistry {
	
	/**
	 * Create a separate tab on the inventory for Ultra items
	 */
	public static CreativeTabs ultraCreativeTab =  null;
	
	/**
	 * Materials
	 */
	public static ToolMaterial ultraToolMaterial = null;
	public static ItemArmor.ArmorMaterial ultraAdvancedArmorMaterial = null;

	/**
	 * Items
	 */
	public static UltraItemBasicSword ultraItemBasicSword = null;
	public static UltraItemTeleportArtifact ultraItemTeleportArtifact = null;
	public static UltraItemBackpack ultraItemBackpackSmall = null;
	public static UltraItemBackpack ultraItemBackpackMedium = null;
	public static UltraItemBackpack ultraItemBackpackLarge = null;
	public static UltraItemGlowStick ultraItemGlowStick = null;
	
	
	/**
	 * ItemArmor
	 */
	public static UltraItemAdvancedArmor ultraItemAdvancedArmorHelmet = null;
	public static UltraItemAdvancedArmor ultraItemAdvancedArmorChestplate = null;
	public static UltraItemAdvancedArmor ultraItemAdvancedArmorLeggings = null;
	public static UltraItemAdvancedArmor ultraItemAdvancedArmorBoots = null;

	/**
	 * GuiScreens
	 */
	public static UltraGuiScreenTeleportWaypoint ultraTeleportWaypointGuiScreen = null;
	
	/**
	 * Misc items and helpers
	 */
	public static HubbyLimitedInventoryItem limitedInventoryItemBackpack = null;
	
	/**
	 * This method is responsible for loading all items, blocks, recipes, potions
	 * and more. Each item is instantiated and then registered with the appropriate
	 * systems to ensure that they function in-game perfectly
	 */
	public static void register() {

	    /**
	     * Creative-tabs
	     */
		ultraCreativeTab = new CreativeTabs(UltraMod.MOD_NAME) {
			@Override
			public Item getTabIconItem() {
				return UltraRegistry.ultraItemBasicSword;
			}
		};
		
		/**
		 * Materials
		 */
		ultraToolMaterial = EnumHelper.addToolMaterial(UltraMod.MOD_NAME, 3, 500, 20.0F, 20.0F, 25);
		ultraAdvancedArmorMaterial = EnumHelper.addArmorMaterial("ultraItemAdvancedArmorMaterial", 
	                                 HubbyUtils.getResourceLocation(UltraMod.MOD_ID, "textures/models/armor/ultra_advanced_armor"), 
	                                 UltraConstants.ULTRA_ITEM_ADVANCED_ARMOR_DURABILITY, UltraConstants.ULTRA_ITEM_ADVANCED_ARMOR_DAMAGE_REDUCTIONS, UltraConstants.ULTRA_ITEM_ADVANCED_ARMOR_ENCHANTABILITY);

		/**
		 * Items
		 */
		ultraItemBasicSword = new UltraItemBasicSword(ultraToolMaterial);
		ultraItemTeleportArtifact = new UltraItemTeleportArtifact();
		ultraItemBackpackSmall = new UltraItemBackpack(BackpackType.SMALL);
		ultraItemBackpackMedium = new UltraItemBackpack(BackpackType.MEDIUM);
		ultraItemBackpackLarge = new UltraItemBackpack(BackpackType.LARGE);
		ultraItemGlowStick = new UltraItemGlowStick();
		
		/**
		 * ItemArmor
		 */
		ultraItemAdvancedArmorHelmet = new UltraItemAdvancedArmor(ultraAdvancedArmorMaterial, UltraConstants.ULTRA_ITEM_ADVANCED_ARMOR_RENDER_INDEX, ArmorType.HELMET);
		ultraItemAdvancedArmorChestplate = new UltraItemAdvancedArmor(ultraAdvancedArmorMaterial, UltraConstants.ULTRA_ITEM_ADVANCED_ARMOR_RENDER_INDEX, ArmorType.CHESTPLATE);
		ultraItemAdvancedArmorLeggings = new UltraItemAdvancedArmor(ultraAdvancedArmorMaterial, UltraConstants.ULTRA_ITEM_ADVANCED_ARMOR_RENDER_INDEX, ArmorType.LEGGINGS);
		ultraItemAdvancedArmorBoots = new UltraItemAdvancedArmor(ultraAdvancedArmorMaterial, UltraConstants.ULTRA_ITEM_ADVANCED_ARMOR_RENDER_INDEX, ArmorType.BOOTS);

		/**
		 * GuiScreens
		 */
		ultraTeleportWaypointGuiScreen = new UltraGuiScreenTeleportWaypoint();
		
		/**
		 * Misc screens and helpers
		 */
		limitedInventoryItemBackpack = new HubbyLimitedInventoryItem("refreshLimitedItemBackpack", 1, ultraItemBackpackSmall);
		
		/**
		 * Init other helpers and systems
		 */
		UltraLightHelper.getInstance();
	}
}
