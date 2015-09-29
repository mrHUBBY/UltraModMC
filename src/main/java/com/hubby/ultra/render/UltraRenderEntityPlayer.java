package com.hubby.ultra.render;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import com.hubby.ultra.items.UltraItemBackpack;
import com.hubby.ultra.models.UltraModelBackpack;
import com.hubby.utils.HubbyUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class catches the player rendering events and renders any custom
 * armor that the player might have equipped such as a backpack
 * @author davidleistiko
 */
public class UltraRenderEntityPlayer {
    
    /**
     * Members
     */
    private UltraModelBackpack _backpackModel = new UltraModelBackpack();
    private ModelBiped _mainModel = null;

    /**
     * Constructor
     */
    public UltraRenderEntityPlayer() {
    }

    /**
     * Called right after we complete rendering the player
     * @param event - the rendering <code>Event</code>
     */
    @SubscribeEvent
    public void onRenderPlayerEnd(RenderPlayerEvent.Post event) {
        // Set the main model
        if (_mainModel == null) {
            RenderPlayer rp = event.renderer;
            _mainModel = rp.getPlayerModel();
        }

        // render the backpack if we have one equipped
        HashMap<Integer, ItemStack> items = (HashMap<Integer, ItemStack>)HubbyUtils.getInventoryItem(UltraItemBackpack.class);
        ItemStack backpack = items.size() > 0 ? (ItemStack)items.values().toArray()[0] : null;
        if (backpack != null && backpack.getItem() instanceof UltraItemBackpack) {
            UltraItemBackpack backpackItem = (UltraItemBackpack)backpack.getItem();
            String texture = UltraItemBackpack.getModelTexture(backpackItem.getBackpackType());
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(texture));
            renderBackpack(event.entityPlayer, event.partialRenderTick);
        }
    }

    /**
     * Renders the current backpack the player is wearing
     * @param player - the <code>EntityPlayer</code>
     * @param partialTick - the current tick of the event
     */
    private void renderBackpack(EntityPlayer player, float partialTick) {
        GL11.glPushMatrix();
        _backpackModel.render(player, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625F);
        GL11.glPopMatrix();
    }
}
