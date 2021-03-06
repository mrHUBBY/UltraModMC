package com.hubby.ultra;

import com.hubby.events.HubbyEventPlayerJoinsGame;
import com.hubby.events.HubbyEventPlayerQuitsGame;
import com.hubby.events.HubbyEventSender;
import com.hubby.ultra.setup.UltraConfigPropertyListener;
import com.hubby.ultra.setup.UltraRegistry;
import com.hubby.utils.HubbyColor;
import com.hubby.utils.HubbyConstants;
import com.hubby.utils.HubbyConstants.LightLevel;
import com.hubby.utils.HubbyUtils;

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

//import tutorial.generic.NitroInterface.ParticleNames;

public class UltraCommandHooks {

    private double lastFireArmorParticleTime = 0.0F;

    /**
     * Called when the player joins the game from interacting with
     * the main menu flow
     * @param event - the event that was fired
     */
    @SubscribeEvent
    public void onPlayerJoinsGame(PlayerEvent.PlayerLoggedInEvent event) {
        
        // fire this event so that others can respond to the player logging out.
        String[] keys = HubbyEventPlayerJoinsGame.getDefaultKeySet();
        Object[] params = new Object[] { event.player };
        HubbyEventSender.getInstance().notifyEvent(HubbyEventPlayerJoinsGame.class, keys, params);
    }
    
    /**
     * Called when the player quits the game to return to the main menu
     * @param event - the event that was fired
     */
    @SubscribeEvent
    public void onPlayerQuitsGame(PlayerEvent.PlayerLoggedOutEvent event) {
        
        // fire this event so that others can respond to the player logging out.
        String[] keys = HubbyEventPlayerQuitsGame.getDefaultKeySet();
        Object[] params = new Object[] { event.player };
        HubbyEventSender.getInstance().notifyEvent(HubbyEventPlayerQuitsGame.class, keys, params);
    }
    
    @SubscribeEvent
    public void onServerTickEvent(TickEvent.ServerTickEvent event) {
    }

    /**
     * This function to called each time a tick occurs in the
     * minecraft world. We can use this function to generate a
     * delta time and elapsed time which we can use for things
     * that need per frame updating.
     * @param event
     */
    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
         //int seconds = HubbyUtils.

//        // Update our delta time
//        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-2"));
//        NitroInterface.onTickEvent(cal.getTimeInMillis());
//
//        // Handle the start tick event
//        if (event.phase == Phase.START) {
//            if (NitroInterface.nitroClientPlayer == null) {
//                NitroInterface.nitroClientPlayer = FMLClientHandler.instance()
//                    .getClient().thePlayer;
//                NitroInterface.nitroClientWorld = FMLClientHandler.instance()
//                    .getClient().theWorld;
//            }
//
//            if (NitroInterface.nitroClientPlayer != null) {
//                updatePlayerLightWithItem();
//            }
//        }
//
//        // Handle the end tick event
//        if (event.phase == Phase.END) {
//            updateNitroLights();
//        }
    }

    /**
     * Handle when the user presses a key
     * @param event - the key event to handle
     */
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        
    	// If the use pressed the teleport gui key then we want to show that gui
    	// to the player now
        if (HubbyUtils.isKeyPressed(UltraConfigPropertyListener.KEY_BINDING_OPEN_TELEPORT_GUI)) {
            UltraRegistry.hubbyPopupMessageGuiScreen.setPopupMessage("This should be an interesting test to see if the popup is working correctly, I bet it is!", 200, HubbyConstants.DEFAULT_POPUP_BORDER, HubbyConstants.DEFAULT_POPUP_BORDER);
            UltraRegistry.hubbyPopupMessageGuiScreen.setCenterText(false);
            UltraRegistry.hubbyPopupMessageGuiScreen.setDropShadow(true);
            UltraRegistry.hubbyPopupMessageGuiScreen.setTextColor(HubbyColor.GREY);
            UltraRegistry.hubbyPopupMessageGuiScreen.setItemIcon(UltraRegistry.ultraItemGlowStick);
            UltraRegistry.hubbyPopupMessageGuiScreen.setAutoCloseTime(2000L, true);
        	Minecraft.getMinecraft().displayGuiScreen(UltraRegistry.hubbyPopupMessageGuiScreen);
        	
        	// TODO:
        	// remove
        	HubbyUtils.setInventoryHotBarItem(0, UltraRegistry.ultraItemBackpackLarge, 1);
        	UltraUtils.attachLightToEntity(HubbyUtils.getClientPlayer(), LightLevel.LEVEL_0);
        }

//        // Handle the user wanting to spawn something
//        if (NitroInterface.isKeyPressed(NitroInterface.nitroKeyRain)) {
//            NitroInterface.toggleRain();
//        }
//
//        // Handle the user wanting to change the time
//        if (NitroInterface.isKeyPressed(NitroInterface.nitroKeyTime)) {
//            long curTime = NitroInterface.getWorldTime();
//            NitroInterface.setWorldTime((curTime + 12000) % 24000);
//        }
//
//        // Handle the user toggling lights
//        if (NitroInterface.isKeyPressed(NitroInterface.nitroKeyLights)) {
//            NitroInterface.toggleLights();
//        }
//
//        // Handle the user pressing the options key
//        if (NitroInterface.isKeyPressed(NitroInterface.nitroKeyOptions)) {
//            Minecraft.getMinecraft().displayGuiScreen(NitroInterface.nitroOptionsGui);
//        }
//        
//        // Handle the user pressing the options key
//        if (NitroInterface.isKeyPressed(NitroInterface.nitroKeyCheat)) {
//        }
//
//        // Handle showuing the backpack gui
//        ItemStack is = NitroSuperGui.utilityInventory.getStackInSlot(1);
//        if (is != null && is.getItem() != null && is.getItem() instanceof NitroItemBackpack) {
//            if (NitroInterface.isKeyPressed(NitroInterface.nitroKeyBackpack)) {
//                Minecraft.getMinecraft().displayGuiScreen(new NitroBackpackGui(NitroInterface.nitroClientPlayer));
//            }
//        }
//
//        // Handle everything
//        if (NitroInterface.isKeyPressed(NitroInterface.nitroKeyEverything)) {
//            Minecraft.getMinecraft().displayGuiScreen(new NitroEverythingGui(NitroInterface.nitroClientPlayer));
//        }
//
//        // Handle super gui
//        if (NitroInterface.isKeyPressed(NitroInterface.nitroKeySuper)) {
//            Minecraft.getMinecraft().displayGuiScreen(new NitroSuperGui(NitroInterface.nitroClientPlayer));
//        }
//
//        if (NitroInterface.isKeyPressed(NitroInterface.nitroKeyNightGoggles)) {
//            NitroItemNightGoggles.gogglesOn = !NitroItemNightGoggles.gogglesOn;
//        }
    }
    
    @SubscribeEvent
    public void onWorldTickEvent(TickEvent.WorldTickEvent event) {
    }

    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) throws Exception {
    	
//        if (event.player instanceof EntityPlayerMP
//            && NitroInterface.nitroServerPlayer == null) {
//            NitroInterface.nitroServerPlayer = (EntityPlayerMP) event.player;
//            NitroInterface.nitroServerWorld = NitroInterface.nitroServerPlayer.worldObj;
//        }
//
//        // handle the start tick
//        if (event.phase == Phase.START) {
//            // Check if this potion has expired
//            if (NitroInterface.nitroPlayer != null
//                && NitroInterface.nitroPlayer
//                    .isPotionActive(NitroInterface.nitroPotion)) {
//                float duration = NitroInterface.nitroPlayer
//                    .getActivePotionEffect(NitroInterface.nitroPotion)
//                    .getDuration();
//                if (duration <= 0) {
//                    NitroInterface.nitroPlayer
//                        .removePotionEffect(NitroInterface.nitroPotion.id);
//                }
//            }
//
//            updateFireImmunity(event);
//            updateFireArmorEffects(event);
//        }
//
//        // Handle the end tick
//        if (event.phase == Phase.END) {
//        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
//        // Only draw the following huds if the night goggles are not in use or turned off
//        if (!NitroItemNightGoggles.AreGogglesEquipeedAndOn()) {
//            if (NitroInterface.showArmorHUD) {
//                NitroInterface.nitroArmorGui.update();
//                NitroInterface.nitroArmorGui.draw();
//            }
//
//            if (NitroInterface.showEntityHUD) {
//                NitroInterface.nitroEntityGui.update();
//                NitroInterface.nitroEntityGui.draw();
//            }
//        }
//        else {
//            NitroItemNightGoggles.drawGoggleOverlay(false);
//        }
    }

//    // Spawns fire particles when wearing the fire armor
//    private void updateFireArmorEffects(TickEvent.PlayerTickEvent event) {
//
//        // add particle effects to fire armor
//        if (NitroInterface.isEntityInFluid(NitroInterface.nitroClientPlayer,
//            Material.water) == false
//            && NitroInterface.isRaining()
//            && NitroInterface
//                .getEquippedArmorCount(NitroFireItemArmor.class) > 0
//            && NitroInterface.getWorldTime() - lastFireArmorParticleTime >= 10.0) {
//            double xPos = event.player.posX;
//            double yPos = event.player.posY + 2.0;
//            double zPos = event.player.posZ;
//
//            if (!event.player.worldObj.isRemote) {
//                NitroInterface.spawnParticle(ParticleNames.Flame, xPos, yPos,
//                    zPos);
//                NitroInterface.spawnParticle(ParticleNames.Smoke, xPos, yPos,
//                    zPos);
//                NitroInterface.spawnParticle(ParticleNames.Smoke2, xPos, yPos,
//                    zPos);
//
//                lastFireArmorParticleTime = NitroInterface.getWorldTime();
//            }
//        }
//    }

//    // Sets the player immune to fire if they are wearing an entire nitro armor
//    // set
//    private void updateFireImmunity(TickEvent.PlayerTickEvent event) {
//        int count = NitroInterface.getEquippedArmorCount(NitroItemArmor.class);
//        Method m;
//        try {
//            m = EntityPlayer.class.getMethod("setIsImmuneToFire", boolean.class);
//            m.invoke(event.player, count == 4);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        ;
//    }

//    // Updates the lightlevel on the player based on the item/armor he is
//    // holding/wearing
//    private void updatePlayerLightWithItem() {
//        if (NitroInterface.nitroClientPlayer != null && NitroInterface.nitroClientPlayer.isEntityAlive() && NitroInterface.nitroLightsOn) {
//            ItemStack armor;
//            int prevLight = NitroInterface.playerLightLevel;
//
//            ItemStack item = NitroInterface.nitroClientPlayer.getCurrentEquippedItem();
//            NitroInterface.playerLightLevel = NitroInterface.getLightFromItemStack(item);
//
//            // Get the light level for the equipped item
//            if (item != null && item.getItem() instanceof INitroLightItem) {
//                NitroInterface.playerLightLevel = ((INitroLightItem) item.getItem()).getLightLevel();
//            }
//
//            // Check our utilities
//            for (int i = 0; i < NitroSuperGui.utilityInventory.inventorySize; ++i) {
//                ItemStack utilStack = NitroSuperGui.utilityInventory.getStackInSlot(i);
//                if (utilStack != null && utilStack.getItem() instanceof INitroLightItem) {
//                    if (!(utilStack.getItem() instanceof NitroItemNightGoggles) || NitroItemNightGoggles.gogglesOn) {
//                        NitroInterface.playerLightLevel = ((INitroLightItem) utilStack.getItem()).getLightLevel();
//                    }
//                }
//            }
//
//            // Check if the player's armor emits light
//            ItemStack[] arr = NitroInterface.nitroClientPlayer.inventory.armorInventory;
//            int len = arr.length;
//            for (int i = 0; i < len; ++i) {
//                armor = arr[i];
//                NitroInterface.playerLightLevel = Math.max(NitroInterface.playerLightLevel, NitroInterface.getLightFromItemStack(armor));
//            }
//
//            // Handle special case light level for the player
//            if (prevLight != 0 && NitroInterface.playerLightLevel != prevLight) {
//                NitroInterface.playerLightLevel = 0;
//            }
//            else if (NitroInterface.nitroClientPlayer.isBurning()) {
//                NitroInterface.playerLightLevel = 15;
//            }
//
//            // finally, enable/disable the light based on the light value calculated above
//            if (NitroInterface.playerLightLevel > 0) {
//                NitroInterface.enableLight(NitroInterface.nitroClientPlayer, NitroInterface.playerLightLevel);
//            }
//            else if (NitroInterface.playerLightLevel < 1) {
//                NitroInterface.disableLight(NitroInterface.nitroClientPlayer);
//            }
//        }
//    }

//    // Update the nitro dynamic lights
//    private void updateNitroLights() {
//        if (NitroInterface.nitroMcInstance.theWorld != null) {
//
//            if (NitroInterface.nitroLights != null) {
//
//                // grab the lights
//                Iterator<NitroLightSourceContainer> iter = NitroInterface.nitroLights
//                    .iterator();
//
//                // iterate over lights
//                while (iter.hasNext()) {
//                    NitroLightSourceContainer tickedLightContainer = iter
//                        .next();
//
//                    if (tickedLightContainer.onUpdate()) {
//                        iter.remove();
//                        NitroInterface.nitroMcInstance.theWorld
//                            .updateLightByType(EnumSkyBlock.Block,
//                                tickedLightContainer.getX(),
//                                tickedLightContainer.getY(),
//                                tickedLightContainer.getZ());
//                    }
//                }
//            }
//        }
//    }
}
