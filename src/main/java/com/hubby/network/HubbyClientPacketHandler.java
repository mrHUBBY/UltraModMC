package com.hubby.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hubby.utils.HubbyConstants.LogChannel;
import com.hubby.utils.HubbyEnumValueInterface;
import com.hubby.utils.HubbyUtils;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;

public class HubbyClientPacketHandler {

    /**
     * Members
     */
    protected EntityPlayerSP _thePlayer;
    protected Map<String, Boolean> _listenChannels = new HashMap<String, Boolean>();
    
    /**
     * Constructor
     * @param channels - the list of channels to listen for
     */
    public HubbyClientPacketHandler(List<String> channels) {
        // by default all channels are enabled
        for (String channel : channels) {
            enableChannel(channel, true);
        }
    }
    
    /**
     * Enables/disables the channel specified.
     * NOTE:
     * This method will also add the channel if it is currently
     * not within the <code>_listenChannels</code> list
     * @param channel
     * @param enable
     */
    public void enableChannel(String channel, boolean enable) {
       _listenChannels.put(channel, enable);
    }

    /**
     * Called when a packet is received on the client
     * @param event - the event coming thru
     * @throws IOException
     */
    @SubscribeEvent
    public void onClientPacket(ClientCustomPacketEvent event) throws IOException {
        String channelName = event.packet.channel();
        
        // Thanks to GoToLink for helping figure out how to get player entity
        NetHandlerPlayClient theNetHandlerPlayClient = (NetHandlerPlayClient)event.handler;
        _thePlayer = HubbyUtils.getClientPlayer();

        // if we cannot do anything with this channel,
        // than a quick bail and exit will help
        if (!_listenChannels.containsKey(channelName) || !_listenChannels.get(channelName)) {
            LogChannel.INFO.log(HubbyServerPacketHandler.class, "Ignoring [client] message on channel %s as is not being listened for...", channelName);
            return;
        }
        
        Enum<? extends HubbyEnumValueInterface> packetType = HubbyNetworkHelper.getPacketTypeForClientEvent(event);  
        String name = HubbyNetworkHelper.getNameForPacketType(packetType);
        
        // Log that we received the message and then process the packet
        LogChannel.INFO.log(HubbyClientPacketHandler.class, "Received [client] message on channel %s", channelName);
        LogChannel.INFO.log(HubbyClientPacketHandler.class, "Processing client packet type %s on the server", name);
        
        // process the packet now
        for (HubbyClientPacketProcessorInterface processor : HubbyNetworkHelper.getClientProcessorsForPacket(packetType)) {
            if (processor.validate(packetType)) {
                processor.processClientPacket(event.packet, event.packet.payload(), event.packet.getTarget(), _thePlayer);
            }
        }
    }
}