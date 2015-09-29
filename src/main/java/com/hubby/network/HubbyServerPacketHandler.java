package com.hubby.network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hubby.utils.HubbyConstants;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;

public class HubbyServerPacketHandler {
    
    /**
     * Members
     */
    public static HubbyServerPacketProcessorInterface _packetProcessor = null;
    protected EntityPlayerMP _thePlayer;
    protected Map<String, Boolean> _listenChannels = new HashMap<String, Boolean>();
    
    /**
     * Constructor
     * @param channels - the list of channels to listen to
     */
    public HubbyServerPacketHandler(List<String> channels) {
        
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
     * Catch the event of when a packet comes in on the server
     * @param event - the event that occurred
     * @throws Exception
     */
    @SubscribeEvent
    public void onServerPacket(ServerCustomPacketEvent event) throws Exception {
        String channelName = event.packet.channel();

        // Thanks to GoToLink for helping figure out how to get player entity
        NetHandlerPlayServer theNetHandlerPlayServer = (NetHandlerPlayServer)event.handler;
        _thePlayer = theNetHandlerPlayServer.playerEntity;

        // if we cannot do anything with this channel,
        // than a quick bail and exit will help
        if (!_listenChannels.containsKey(channelName) || !_listenChannels.get(channelName)) {
            HubbyConstants.LogChannel.INFO.log(HubbyServerPacketHandler.class, "Ignoring [server] on channel %s as is not being listened for...", channelName);
            return;
        }
        
        // Log that we received the message and then process the packet
        HubbyConstants.LogChannel.INFO.log(HubbyServerPacketHandler.class, "Received [server] message on channel %s", channelName);
        if (_packetProcessor != null) {
            _packetProcessor.processServerPacket(event.packet, event.packet.payload(), event.packet.getTarget(), _thePlayer);
        }
    }
}
