package com.hubby.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hubby.network.HubbyNetworkHelper.ProcessPacketResult;
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
        
        Enum<? extends HubbyEnumValueInterface> packetType = HubbyNetworkHelper.getPacketTypeForNetworkEvent(event);  
        String name = HubbyNetworkHelper.getNameForPacketType(packetType);
        float time = HubbyNetworkHelper.getElapsedTimeForNetworkEvent(event);
        
        // Log that we received the message and then process the packet
        LogChannel.INFO.log(HubbyClientPacketHandler.class, "Received packet of type %s on the client for player %s at time %d in %.4f seconds", name, _thePlayer.getName(), HubbyUtils.getTimestamp(), time);
        
        // process the packet now
        for (HubbyClientPacketProcessorInterface processor : HubbyNetworkHelper.getClientProcessorsForPacket(packetType)) {
            if (processor.validate(packetType)) {
                ProcessPacketResult result = processor.processClientPacket(event.packet, event.packet.payload(), event.packet.getTarget(), _thePlayer);
                if (result == ProcessPacketResult.STOP) {
                    break;
                }
            }
        }
    }
}