package com.hubby.network;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This interface defines the single method that should
 * be implemented by others who want to handle packets
 * coming down on the client
 * @author davidleistiko
 */
public interface HubbyClientPacketProcessorInterface {
    
    /**
     * This method sets the parameters that are needed for reading a client packet
     * @param packet - the packet coming down
     * @param buffer - the buffer of data
     * @param side - are we the client or server?
     * @throws IOException
     */
    boolean processClientPacket(FMLProxyPacket packet, ByteBuf buffer, Side side) throws IOException;
}
