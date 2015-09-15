package com.hubby.ultra.items;

import com.hubby.shared.utils.INamedObject;
import com.hubby.shared.utils.Utils;
import com.hubby.ultra.UltraTeleportManagerGuiScreen;
import com.hubby.ultra.setup.UltraMod;
import com.hubby.ultra.setup.UltraRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class UltraItemTeleportArtifact extends Item implements INamedObject {
	
	/**
	 * The item name
	 */
	public static final String NAME = "ultraItemTeleportArtifact";

	/**
	 * Default Constructor
	 */
	public UltraItemTeleportArtifact() {
		this.setUnlocalizedName(NAME);
		this.setCreativeTab(UltraRegistry.ultraCreativeTab);
		
		// actually register the item with forge and mc for rendering
		Utils.registerNamedItem(UltraMod.MOD_ID, this);
	}
	
	/**
	 * Accessor that returns the item name
	 * @return String - the item name
	 */
	public String getName() {
		return NAME;
	}

	/**
	 * Handle when the player uses the right-click for this item
	 * @param itemStack - the item stack containing this item
	 * @param world - the world
	 * @param player - the player who performed the right click
	 */
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			// TODO:
			// Add particle effects for an effect
		    //NitroInterface.spawnParticle(NitroInterface.ParticleNames.Teleport, par3EntityPlayer.posX, par3EntityPlayer.posY + 1, par3EntityPlayer.posZ);
		    //NitroInterface.spawnParticle(NitroInterface.ParticleNames.Teleport, par3EntityPlayer.posX, par3EntityPlayer.posY + 1, par3EntityPlayer.posZ);
		    //NitroInterface.spawnParticle(NitroInterface.ParticleNames.Teleport, par3EntityPlayer.posX, par3EntityPlayer.posY + 1, par3EntityPlayer.posZ);
		    //NitroInterface.spawnParticle(NitroInterface.ParticleNames.Teleport, par3EntityPlayer.posX, par3EntityPlayer.posY + 1, par3EntityPlayer.posZ);
		    //NitroInterface.spawnParticle(NitroInterface.ParticleNames.Teleport, par3EntityPlayer.posX, par3EntityPlayer.posY + 1, par3EntityPlayer.posZ);
		    Minecraft.getMinecraft().displayGuiScreen(new UltraTeleportManagerGuiScreen());
		}
		return itemStack;
	}
}
