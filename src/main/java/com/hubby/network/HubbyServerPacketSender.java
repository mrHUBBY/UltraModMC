package com.hubby.network;

import java.util.Map;

import com.hubby.utils.HubbyConstants.LogChannel;
import com.hubby.utils.HubbyEnumValueInterface;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

/**
 * The purpose of this class is to serve as a packet creator
 * for the server side, and to also provide a mechanism for
 * sending the packets to all of the clients
 * @author davidleistiko
 */
public class HubbyServerPacketSender {

    /**
     * Constructor
     */
    public HubbyServerPacketSender() {
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
        FMLProxyPacket packet = createServerPacket(packetType, channelName, args);
        sendToAllClients(packet, HubbyNetworkHelper.getChannelForName(channelName), packetType, null);
    }
    
    /**
     * This version of <code>sendPacket</code> only sends the message to the client if the player
     * on that client is near the <code>TargetPoint</code>
     * @param pt - the point where the sendPacket will originate from
     * @param packetType - the packet type we are sending
     * @param channelName - the channel name to send on
     * @param args - the args to pass along
     */
    public static void sendPacket(TargetPoint pt,
                                  Enum<? extends HubbyEnumValueInterface> packetType, 
                                  String channelName, 
                                  Map<String, Object> args) {
        FMLProxyPacket packet = createServerPacket(packetType, channelName, args);
        sendToAllClients(packet, HubbyNetworkHelper.getChannelForName(channelName), packetType, pt);
    }

    /**
     * This version of <code>sendPacket</code> only sends the message to one client which is identified by
     * the server player that is passed in.
     * @param player - the player to send the packet to
     * @param packetType - the packet type
     * @param channelName - the channel name we are sending on
     * @param args - the args to pass along with the packet
     */
    public static void sendPacket(EntityPlayerMP player,
                                  Enum<? extends HubbyEnumValueInterface> packetType,
                                  String channelName, 
                                  Map<String, Object> args) {
        FMLProxyPacket packet = createServerPacket(packetType, channelName, args);
        sendToClient(packet, HubbyNetworkHelper.getChannelForName(channelName), packetType, player);
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
    protected static FMLProxyPacket createServerPacket(Enum<? extends HubbyEnumValueInterface> packetType, String channelName, Map<String, Object> args) {
        
        // first, let's make sure that the user has registered this packet type
        if (!HubbyNetworkHelper.isPacketTypeRegistered(packetType)) {
            LogChannel.WARNING.log(HubbyServerPacketSender.class, "Failed to create client packet; the packet type %s has not been registered!", "");
        }
        
        // log that we are handling this packet now
        LogChannel.INFO.log(
            HubbyServerPacketSender.class, 
            "Generaring server packet of type %s to send to the client", 
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
        HubbyServerPacketWriterInterface writer = HubbyNetworkHelper.getServerWriterForPacket(packetType);
        if (writer == null) {
            LogChannel.WARNING.log(HubbyServerPacketSender.class, "Could not send packet of type %s to the client(s); no buffer writer exists for that packet type!");
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
     * This function sends the specified packet to all clients connected to this server
     * @param packet - the packet to send
     * @param channel - the channel to send it on
     * @param packetType - the packet type we are sending
     */
    protected static void sendToAllClients(FMLProxyPacket packet, FMLEventChannel channel, Enum<? extends HubbyEnumValueInterface> packetType, TargetPoint pt) {
        String name = HubbyNetworkHelper.getNameForPacketType(packetType);
        LogChannel.INFO.log(HubbyServerPacketSender.class, "Sending packet of type %s to all clients", name);
        if (pt == null) {
            channel.sendToAll(packet);
        }
        else {
            channel.sendToAllAround(packet, pt);
        }
    }
    
    /**
     * Sends the server packet only to the client specified by the server player that is passed in
     * @param packet - the packet to send
     * @param channel - the channel to send on
     * @param packetType - the packet type
     * @param player - the player to send the packet to
     */
    protected static void sendToClient(FMLProxyPacket packet, FMLEventChannel channel, Enum<? extends HubbyEnumValueInterface> packetType, EntityPlayerMP player) {
        String name = HubbyNetworkHelper.getNameForPacketType(packetType);
        LogChannel.INFO.log(HubbyServerPacketSender.class, "Sending packet of type %s to all clients", name);
        channel.sendTo(packet, player);
    }
}
