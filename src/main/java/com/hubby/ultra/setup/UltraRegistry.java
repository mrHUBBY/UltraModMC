package com.hubby.ultra.setup;

import com.hubby.shared.utils.HubbyConstants.ArmorType;
import com.hubby.shared.utils.HubbyUtils;
import com.hubby.ultra.UltraConstants;
import com.hubby.ultra.UltraConstants.BackpackType;
import com.hubby.ultra.UltraTeleportWaypointGuiScreen;
import com.hubby.ultra.items.UltraItemAdvancedArmor;
import com.hubby.ultra.items.UltraItemBackpackArmor;
import com.hubby.ultra.items.UltraItemBasicSword;
import com.hubby.ultra.items.UltraItemTeleportArtifact;

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
	public static ItemArmor.ArmorMaterial[] ultraBackpackArmorMaterial = null;

	/**
	 * Items
	 */
	public static UltraItemBasicSword ultraItemBasicSword = null;
	public static UltraItemTeleportArtifact ultraItemTeleportArtifact = null;
	
	/**
	 * ItemArmor
	 */
	public static UltraItemAdvancedArmor ultraItemAdvancedArmorHelmet = null;
	public static UltraItemAdvancedArmor ultraItemAdvancedArmorChestplate = null;
	public static UltraItemAdvancedArmor ultraItemAdvancedArmorLeggings = null;
	public static UltraItemAdvancedArmor ultraItemAdvancedArmorBoots = null;
	public static UltraItemBackpackArmor ultraItemBackpackArmorSmall = null;
	public static UltraItemBackpackArmor ultraItemBackpackArmorMedium = null;
	public static UltraItemBackpackArmor ultraItemBackpackArmorLarge = null;

	/**
	 * GuiScreens
	 */
	public static UltraTeleportWaypointGuiScreen ultraTeleportWaypointGuiScreen = null;
	
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
		ultraBackpackArmorMaterial = new ItemArmor.ArmorMaterial[] { 
		                                    EnumHelper.addArmorMaterial("ultraItemBackpackArmorMaterial",
		                                        null, // we pass in null for the ResourceLocation since we draw the backpack manually
		                                        UltraConstants.ULTRA_ITEM_BACKPACK_ARMOR_DURABILITY, UltraConstants.ULTRA_ITEM_BACKPACK_ARMOR_DAMAGE_REDUCTIONS, UltraConstants.ULTRA_ITEM_BACKPACK_ARMOR_ENCHANTABILITY),
		                                    EnumHelper.addArmorMaterial("ultraItemBackpackArmorMaterial",
	                                            null, // we pass in null for the ResourceLocation since we draw the backpack manually
	                                            UltraConstants.ULTRA_ITEM_BACKPACK_ARMOR_DURABILITY, UltraConstants.ULTRA_ITEM_BACKPACK_ARMOR_DAMAGE_REDUCTIONS, UltraConstants.ULTRA_ITEM_BACKPACK_ARMOR_ENCHANTABILITY),
		                                    EnumHelper.addArmorMaterial("ultraItemBackpackArmorMaterial",
	                                            null, // we pass in null for the ResourceLocation since we draw the backpack manually
	                                            UltraConstants.ULTRA_ITEM_BACKPACK_ARMOR_DURABILITY, UltraConstants.ULTRA_ITEM_BACKPACK_ARMOR_DAMAGE_REDUCTIONS, UltraConstants.ULTRA_ITEM_BACKPACK_ARMOR_ENCHANTABILITY),
		                             };
		

		/**
		 * Items
		 */
		ultraItemBasicSword = new UltraItemBasicSword(ultraToolMaterial);
		ultraItemTeleportArtifact = new UltraItemTeleportArtifact();
		
		/**
		 * ItemArmor
		 */
		ultraItemAdvancedArmorHelmet = new UltraItemAdvancedArmor(ultraAdvancedArmorMaterial, UltraConstants.ULTRA_ITEM_ADVANCED_ARMOR_RENDER_INDEX, ArmorType.HELMET);
		ultraItemAdvancedArmorChestplate = new UltraItemAdvancedArmor(ultraAdvancedArmorMaterial, UltraConstants.ULTRA_ITEM_ADVANCED_ARMOR_RENDER_INDEX, ArmorType.CHESTPLATE);
		ultraItemAdvancedArmorLeggings = new UltraItemAdvancedArmor(ultraAdvancedArmorMaterial, UltraConstants.ULTRA_ITEM_ADVANCED_ARMOR_RENDER_INDEX, ArmorType.LEGGINGS);
		ultraItemAdvancedArmorBoots = new UltraItemAdvancedArmor(ultraAdvancedArmorMaterial, UltraConstants.ULTRA_ITEM_ADVANCED_ARMOR_RENDER_INDEX, ArmorType.BOOTS);
		ultraItemBackpackArmorSmall = new UltraItemBackpackArmor(ultraBackpackArmorMaterial[BackpackType.SMALL.getValue()], UltraConstants.ULTRA_ITEM_BACKPACK_ARMOR_RENDER_INDEX, BackpackType.SMALL);
		ultraItemBackpackArmorMedium = new UltraItemBackpackArmor(ultraBackpackArmorMaterial[BackpackType.MEDIUM.getValue()], UltraConstants.ULTRA_ITEM_BACKPACK_ARMOR_RENDER_INDEX, BackpackType.MEDIUM);
		ultraItemBackpackArmorLarge = new UltraItemBackpackArmor(ultraBackpackArmorMaterial[BackpackType.LARGE.getValue()], UltraConstants.ULTRA_ITEM_BACKPACK_ARMOR_RENDER_INDEX, BackpackType.LARGE);
		

		/**
		 * GuiScreens
		 */
		ultraTeleportWaypointGuiScreen = new UltraTeleportWaypointGuiScreen();
	}
}
