package com.hubby.network;

import java.util.Map;

import com.hubby.utils.HubbyConstants.LogChannel;
import com.hubby.utils.HubbyEnumValueInterface;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

/**
 * Client packet writer that writes data about a player inventory event
 * @author davidleistiko
 */
public class HubbyClientPacketWriterPlayerInventory extends HubbyClientPacketWriterInterface {

    /**
     * Constructor
     */
    public HubbyClientPacketWriterPlayerInventory(Enum<? extends HubbyEnumValueInterface> packetType) {
        super(packetType);
    }
    
    /**
     * Validates that the args are correct
     * @param packetType - the packet type to verify
     * @param args - the map of args to validate
     * @return boolean - did we successfully validate?
     */
    @Override
    public boolean validate(Enum<? extends HubbyEnumValueInterface> packetType, Map<String, Object> args) {
        boolean result = super.validate(packetType, args);
        if (!result) {
            return result;
        }
        
        result = args.size() == 2 && args.containsKey("stack") && args.containsKey("slot");
        if (!result) {
            LogChannel.ERROR.log(HubbyClientPacketWriterPlayerInventory.class, 
                "Client writer was expecting args for packet type %s; got the wrong args %s instead!",
                HubbyNetworkHelper.getNameForPacketType(_packetType), args.toString());
        }
        return result;
    }

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
