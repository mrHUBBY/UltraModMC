package com.hubby.network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hubby.network.HubbyNetworkHelper.ProcessPacketResult;
import com.hubby.utils.HubbyConstants.LogChannel;
import com.hubby.utils.HubbyEnumValueInterface;
import com.hubby.utils.HubbyUtils;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;

public class HubbyServerPacketHandler {
    
    /**
     * Members
     */
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
            LogChannel.INFO.log(HubbyServerPacketHandler.class, "Ignoring [server] on channel %s as is not being listened for...", channelName);
            return;
        }

        Enum<? extends HubbyEnumValueInterface> packetType = HubbyNetworkHelper.getPacketTypeForNetworkEvent(event);  
        String name = HubbyNetworkHelper.getNameForPacketType(packetType);
        float time = HubbyNetworkHelper.getElapsedTimeForNetworkEvent(event);
        
        // Log that we received the message and then process the packet
        LogChannel.INFO.log(HubbyServerPacketHandler.class, "Received packet of type %s on the server at time %d in %.4f seconds", name, HubbyUtils.getTimeUTC(), time);
 
        // process the packet now
        for (HubbyServerPacketProcessorInterface processor : HubbyNetworkHelper.getServerProcessorsForPacket(packetType)) {
            if (processor.validate(packetType)) {
                ProcessPacketResult result = processor.processServerPacket(event.packet, event.packet.payload(), event.packet.getTarget(), _thePlayer);
                if (result == ProcessPacketResult.STOP) {
                    break;
                }
            }
        }
    }
}