package com.hubby.network;

import java.util.Map;

import com.hubby.utils.HubbyConstants.LogChannel;
import com.hubby.utils.HubbyEnumValueInterface;

import net.minecraft.network.PacketBuffer;

/**
 * The purpose of this class is to define an interface for adhering to how
 * packet data should be written before being sent off to the client.
 * @author davidleistiko
 */
public abstract class HubbyServerPacketWriterInterface {

    /**
     * The type of packet that this writer handles
     */
    protected Enum<? extends HubbyEnumValueInterface> _packetType;
    
    /**
     * Constructor
     * @param packetType - the type of packet that we handle
     */
    public HubbyServerPacketWriterInterface(Enum<? extends HubbyEnumValueInterface> packetType) {
        _packetType = packetType;
    }
    
    /**
     * Returns the packet type that we respond to
     * @return Enum - the packet type enum value
     */
    public Enum<? extends HubbyEnumValueInterface> getSupportedPacketType() {
        return _packetType;
    }
    
    /**
     * Validates that the args passed in are of the correct type and size
     * @param args - the args to validate
     * @return boolean - were the args correct?
     */
    public boolean validate(Enum<? extends HubbyEnumValueInterface> packetType, Map<String, Object> args) {
        if (_packetType == packetType) {
            return true;
        }
        
        // log the error and return false
        LogChannel.ERROR.log(HubbyServerPacketWriterInterface.class, 
            "Server writer was sent the wrong packet type; expected %s but got %s!",
            HubbyNetworkHelper.getNameForPacketType(_packetType),
            HubbyNetworkHelper.getNameForPacketType(packetType));
        
        return false;
    }
    
    /**
     * This method must be implemented and needs to support the writing of the args
     * contained within the map to the <code>PacketBuffer</buffer> object
     * @param buffer - the buffer to write to
     * @param args - the args to write to the buffer
     */
    abstract public void writeToBuffer(PacketBuffer buffer, Map<String, Object> args); 
}
