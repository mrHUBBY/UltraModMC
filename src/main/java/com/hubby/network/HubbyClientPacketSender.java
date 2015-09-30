package com.hubby.network;

import java.util.Map;

import com.hubby.utils.HubbyConstants.LogChannel;
import com.hubby.utils.HubbyEnumValueInterface;

import net.minecraft.network.PacketBuffer;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class HubbyClientPacketSender {
    
    /**
     * Constructor
     */
    public HubbyClientPacketSender() {
    }
    
    /**
     * Generic function that allows for the sending of any type of packet as
     * identified by the <code>packetType</code> parameter with custom settings
     * as stored in the <code>args</code> parameter.
     * @param packetType - the packet type we are sending
     * @param channel - the channel to send it on
     * @param channelName - the specific channel name within the channel to use
     * @param args - the data needing to be sent across the network
     */
    public static void sendPacket(Enum<? extends HubbyEnumValueInterface> packetType, 
                                  String channelName, 
                                  Map<String, Object> args) {
        FMLProxyPacket packet = createClientPacket(packetType, channelName, args);
        sendToServer(packet, HubbyNetworkHelper.getChannelForName(channelName), packetType);
    }

    /**
     * Primary access point for generating client packets to
     * send to the server.
     * @param packetType - the type of packet we are sending (must be an enum that implements the value interface)
     * @param channelName - the name of the channel to send it on
     * @param args - the data used to populate the buffer we send with the packet
     * @return FMLProxyPacket - the final packet that will be sent along the network
     */
    @SuppressWarnings("resource")
    protected static FMLProxyPacket createClientPacket(Enum<? extends HubbyEnumValueInterface> packetType, String channelName, Map<String, Object> args) {
        
        // first, let's make sure that the user has registered this packet type
        if (!HubbyNetworkHelper.isPacketTypeRegistered(packetType)) {
            LogChannel.WARNING.log(HubbyClientPacketSender.class, "Failed to create client packet; the packet type %s has not been registered!", "");
        }
        
        // log that we are handling this packet now
        LogChannel.INFO.log(
            HubbyClientPacketSender.class, 
            "Generaring client packet of type %s to send to the server", 
            HubbyNetworkHelper.getNameForPacketType(packetType));
        
        // Construct the initial buffer to be populated by the specific
        // packet type function
        ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());
        PacketBuffer buffer = new PacketBuffer(bbos.buffer());
        
        // as a header, we always write the packet type first
        // so that on the other side of network land we can
        // identify which packet we just received.
        buffer.writeInt(((HubbyEnumValueInterface)packetType).getValue());
        
        // lookup the writer to add
        HubbyClientPacketWriterInterface writer = HubbyNetworkHelper.getClientWriterForPacket(packetType);
        if (writer == null) {
            LogChannel.WARNING.log(HubbyClientPacketSender.class, "Could not send packet of type %s to the server; no buffer writer exists for that packet type!");
            return null;
        }
        
        // validate that we are using the correct writer...
        if (!writer.validate(packetType, args)) {
            return null;
        }
        
        // populate the buffer with the values that we need
        writer.writeToBuffer(buffer, args);
        
        // create the packet and return it
        FMLProxyPacket thePacket = new FMLProxyPacket(buffer, channelName);
        return thePacket;
    }

    /**
     * Sends the packet passed in along the given channel
     * as its on its way to the server
     * @param packet - the packet to send
     * @param channel - the channel to send it on
     */
    protected static void sendToServer(FMLProxyPacket packet, FMLEventChannel channel, Enum<? extends HubbyEnumValueInterface> packetType) {
        String name = HubbyNetworkHelper.getNameForPacketType(packetType);
        LogChannel.INFO.log(HubbyClientPacketSender.class, "Sending packet of type %s to the server", name);
        channel.sendToServer(packet);
    }
}