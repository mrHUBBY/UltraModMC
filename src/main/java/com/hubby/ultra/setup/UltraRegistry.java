package com.hubby.ultra.setup;

import com.hubby.ultra.UltraTeleportWaypointGuiScreen;
import com.hubby.ultra.items.UltraItemBasicSword;
import com.hubby.ultra.items.UltraItemTeleportArtifact;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;

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
	
	// region - Materials
	public static ToolMaterial ultraToolMaterial = null;
	// endregion
	
	// region - Items
	public static UltraItemBasicSword ultraItemBasicSword = null;
	public static UltraItemTeleportArtifact ultraItemTeleportArtifact = null;
	// endregion
	
	// region - GuiScreen's
	public static UltraTeleportWaypointGuiScreen ultraTeleportWaypointGuiScreen = null;
	
	/**
	 * This method is responsible for loading all items, blocks, recipes, potions
	 * and more. Each item is instantiated and then registered with the appropriate
	 * systems to ensure that they function in-game perfectly
	 */
	public static void register() {

		// creates the ultra creative tab that will be used by all ultra items
		// as the tab that they render on when viewing the inventory in creative mode
		ultraCreativeTab = new CreativeTabs(UltraMod.MOD_NAME) {
			@Override
			public Item getTabIconItem() {
				return UltraRegistry.ultraItemBasicSword;
			}
		};
		
		// region - Materials
		ultraToolMaterial = EnumHelper.addToolMaterial(UltraMod.MOD_NAME, 3, 500, 20.0F, 20.0F, 25);
		// endregion
		
		// region - Items
		ultraItemBasicSword = new UltraItemBasicSword(ultraToolMaterial);
		ultraItemTeleportArtifact = new UltraItemTeleportArtifact();
		// endregion
		
		// region - Gui Screens
		ultraTeleportWaypointGuiScreen = new UltraTeleportWaypointGuiScreen();
		// endregion
	}
}
