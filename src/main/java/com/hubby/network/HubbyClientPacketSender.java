package com.hubby.network;

import java.util.Map;

import com.hubby.network.HubbyNetworkHelper.HubbyClientPacketWriterInterface;
import com.hubby.utils.HubbyConstants;
import com.hubby.utils.HubbyConstants.HubbyClientPacketType;
import com.hubby.utils.HubbyEnumValueInterface;

import net.minecraft.item.ItemStack;
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
     * Build and send the packet that syncs the player inventory from
     * the client side to the server side
     */
    public static void sendPacketPlayerInventory(FMLEventChannel channel, String channelName, Map<String, Object> args) {
        FMLProxyPacket packet = createClientPacket(HubbyClientPacketType.PLAYER_INVENTORY, channelName, args);
        sendToServer(packet, channel, HubbyClientPacketType.PLAYER_INVENTORY);
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
            HubbyConstants.LogChannel.WARNING.log(HubbyClientPacketSender.class, "Failed to create client packet; the packet type %s has not been registered!", "");
        }
        
        // log that we are handling this packet now
        HubbyConstants.LogChannel.INFO.log(
            HubbyClientPacketSender.class, 
            "Generaring client packet of type %s to send to the server", 
            HubbyNetworkHelper.getNameForEnum(packetType));
        
        // Construct the initial buffer to be populated by the specific
        // packet type function
        ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());
        PacketBuffer buffer = new PacketBuffer(bbos.buffer());
        
        // as a header, we always write the packet type first
        // so that on the other side of network land we can
        // identify which packet we just received.
        buffer.writeInt(packetType.ordinal());
        
        // lookup the writer to add
        HubbyClientPacketWriterInterface writer = HubbyNetworkHelper.getPacketWriterForPacket(packetType);
        if (writer == null) {
            HubbyConstants.LogChannel.WARNING.log(HubbyClientPacketSender.class, "Could not send packet of type %s to the server; no buffer writer exists for that packet type!");
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
        String name = HubbyNetworkHelper.getNameForEnum(packetType);
        HubbyConstants.LogChannel.INFO.log(HubbyClientPacketSender.class, "Sending packet of type %s to the server", name);
        channel.sendToServer(packet);
    }
    
    /**
     * Generates the packet needed in order to sync player inventory
     * information from the client to the server and vice versa
     * @param packetType - the packet type to send
     * @param channel - the channel name we want to send the packet on
     * @param args - the args used to build the packet
     * @return
     */
    protected static void writePlayerInventoryToBuffer(PacketBuffer buffer, Map<String, Object> args) {
        // Read values from the args map
        ItemStack stack = (ItemStack)args.get("stack");
        Integer slot = (Integer)args.get("slot");
        Integer offset = (Integer)args.get("offset");

        // Add custom args to the buffer
        buffer.writeItemStackToBuffer(stack);
        buffer.writeInt(slot);
        buffer.writeInt(offset);
    }
}