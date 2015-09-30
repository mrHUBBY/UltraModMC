package com.hubby.network;

import java.util.Map;

import com.hubby.network.HubbyNetworkHelper.HubbyClientPacketWriterInterface;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

/**
 * Client packet writer that writes data about a player inventory event
 * @author davidleistiko
 */
public class HubbyClientPacketWriterPlayerInventory implements HubbyClientPacketWriterInterface {

    /**
     * Writes the game data to the buffer to be sent along with the packet
     * @param buffer - the buffer to write to
     * @param args - contains the game specific data needing to be sent
     */
    @Override
    public void writeToBuffer(PacketBuffer buffer, Map<String, Object> args) {
        // Read values from the args map
        ItemStack stack = (ItemStack)args.get("stack");
        Integer slot = (Integer)args.get("slot");

        // Add custom args to the buffer
        buffer.writeItemStackToBuffer(stack);
        buffer.writeInt(slot);
    }
}
