package com.hubby.network;

import java.util.HashMap;
import java.util.Map;

import com.hubby.utils.HubbyConstants.HubbyClientPacketType;

import net.minecraft.item.ItemStack;

/**
 * Utility class used to help send common network data more easily
 * from other code
 * @author davidleistiko
 */
public class HubbyClientPacketSenderHelper {

    /**
     * Sends the player inventory packet to the server
     * @param stack - the <code>ItemStack</code> to send
     * @param slot - the slot index as to where to place the <code>ItemStack</code>
     */
    public static void sendPacketPlayerInventory(String channelName, ItemStack stack, int slot) {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("stack", stack);
        args.put("slot", slot);
        HubbyClientPacketSender.sendPacket(HubbyClientPacketType.PLAYER_INVENTORY, channelName, args);
    }
}
