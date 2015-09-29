package com.hubby.network;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This interface defines the single method that should
 * be implemented by others who want to handle packets
 * coming down on the server
 * @author davidleistiko
 */
public interface HubbyServerPacketProcessorInterface {
    
    /**
     * Defines the entry point for server packet handlers that want to process
     * the event that came down with the packet
     * @param packet - the packet coming down
     * @param buffer - the buffer of data
     * @param side - the side of the process
     * @param player - the player (server version)
     * @throws Exception
     */
    void processServerPacket(FMLProxyPacket packet, ByteBuf buffer, Side side, EntityPlayerMP player) throws IOException;
}
