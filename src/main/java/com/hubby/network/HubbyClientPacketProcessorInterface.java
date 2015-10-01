package com.hubby.network;

import java.io.IOException;

import com.hubby.utils.HubbyConstants.LogChannel;
import com.hubby.utils.HubbyEnumValueInterface;

import net.minecraft.client.entity.EntityPlayerSP;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This interface defines the single method that should
 * be implemented by others who want to handle packets
 * coming down on the client
 * @author davidleistiko
 */
public abstract class HubbyClientPacketProcessorInterface {
    
    protected Enum<? extends HubbyEnumValueInterface> _packetType;
    
    /**
     * Constructor
     * @param packet - the packet type that this processor is looking for
     */
    public HubbyClientPacketProcessorInterface(Enum<? extends HubbyEnumValueInterface> packetType) {
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
     * Validates that this processor is processing the correct packet type
     * @param packetType - the packet type to validate
     * @return boolean - are we processing the correct packet type?
     */
    public boolean validate(Enum<? extends HubbyEnumValueInterface> packetType) {
        if (_packetType == packetType) {
            return true;
        }
        
        // log the error and return false
        LogChannel.ERROR.log(HubbyClientPacketProcessorInterface.class, 
            "Client processor was sent the wrong packet type; expected %s but got %s!",
            HubbyNetworkHelper.getNameForPacketType(_packetType),
            HubbyNetworkHelper.getNameForPacketType(packetType));
        
        return false;
    }
    
    /**
     * This method sets the parameters that are needed for reading a client packet
     * @param packet - the packet coming down
     * @param buffer - the buffer of data
     * @param side - are we the client or server?
     * @param player - the player
     * @throws IOException
     */
    abstract public boolean processClientPacket(FMLProxyPacket packet, ByteBuf buffer, Side side, EntityPlayerSP player) throws IOException;
}
