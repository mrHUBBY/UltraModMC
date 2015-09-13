package com.hubby.ultra.setup;

import com.hubby.shared.utils.Utils;
import com.hubby.ultra.items.UltraItemBasicSword;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;

import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;

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
	// endregion
	
	public static void register() {

		ultraCreativeTab = new CreativeTabs(UltraMod.MOD_NAME) {
			@Override
			public Item getTabIconItem() {
				return UltraRegistry.ultraItemBasicSword;
			}
		};
		
		ultraToolMaterial = EnumHelper.addToolMaterial(UltraMod.MOD_ID, 3, 500, 20.0F, 20.0F, 25);
		ultraItemBasicSword = new UltraItemBasicSword(ultraToolMaterial);
		
		//RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		//renderItem.getItemModelMesher().register(ultraItemBasicSword, 0, new ModelResourceLocation(Utils.getResourceLocation(UltraMod.MOD_ID, ultraItemBasicSword.getName()), "inventory"));
	}
}
