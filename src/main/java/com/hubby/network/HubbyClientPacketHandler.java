package com.hubby.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hubby.utils.HubbyConstants.LogChannel;

import net.minecraft.client.entity.EntityPlayerSP;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;

public class HubbyClientPacketHandler {

    /**
     * Members
     */
    public static HubbyClientPacketProcessorInterface _packetProcessor = null;
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

        // if we cannot do anything with this channel,
        // than a quick bail and exit will help
        if (!_listenChannels.containsKey(channelName) || !_listenChannels.get(channelName)) {
            LogChannel.INFO.log(HubbyServerPacketHandler.class, "Ignoring [client] message on channel %s as is not being listened for...", channelName);
            return;
        }
        
        LogChannel.INFO.log(HubbyClientPacketHandler.class, "Received [client] message on channel %s", channelName);
        _packetProcessor.processClientPacket(event.packet, event.packet.payload(), event.packet.getTarget());
    }
}