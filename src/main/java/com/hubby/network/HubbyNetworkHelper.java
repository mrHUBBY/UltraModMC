package com.hubby.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.hubby.utils.HubbyConstants.HubbyClientPacketType;
import com.hubby.utils.HubbyConstants.LogChannel;
import com.hubby.utils.HubbyEnumValueInterface;

import net.minecraft.network.PacketBuffer;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

/**
 * Simple helper class for network functionality
 */
public class HubbyNetworkHelper {

    /**
     * This map stores the relationship between a packet name and a
     * corresponding enum value
     */
    private static final Map<String, Enum<? extends HubbyEnumValueInterface>> PACKET_TYPES = new HashMap<String, Enum<? extends HubbyEnumValueInterface>>();
    
    /**
     * Stores registered packet writers
     */
    private static final Map<String, HubbyClientPacketWriterInterface> CLIENT_PACKET_WRITERS = new HashMap<String, HubbyClientPacketWriterInterface>();
    
    /**
     * Registered packet processors for the server based on packet type
     */
    private static final Map<String, ArrayList<HubbyServerPacketProcessorInterface>> SERVER_PACKET_PROCESSORS = new HashMap<String, ArrayList<HubbyServerPacketProcessorInterface>>();
    
    /**
     * This is the name of the default channel to be used with most network
     * commands that will be issued during the course of the mod
     */
    private static final String HUBBY_NETWORK_CHANNEL_NAME = "{hubby-channel}";
    
    /**
     * This is the default channel that uses the default channel
     * name, which is to be used for most network commands unless
     * something more custom is needed
     */
    private static final FMLEventChannel HUBBY_NETWORK_CHANNEL = NetworkRegistry.INSTANCE.newEventDrivenChannel(HubbyNetworkHelper.HUBBY_NETWORK_CHANNEL_NAME);;
    
    /**
     * List of all registered channel names
     */
    private static final ArrayList<String> REGISTERED_CHANNEL_NAMES = new ArrayList<String>();
    
    /**
     * List of all registered channels
     */
    private static final ArrayList<FMLEventChannel> REGISTERED_CHANNELS = new ArrayList<FMLEventChannel>();
    
    /**
     * The default packet handler that will aid in sending packets from the client to the server
     */
    private static final HubbyClientPacketHandler HUBBY_CLIENT_PACKET_HANDLER = new HubbyClientPacketHandler(new ArrayList());
    
    /**
     * The default packet handler that will aid in the receiving of packets from the client
     */
    private static final HubbyServerPacketHandler HUBBY_SERVER_PACKET_HANDLER = new HubbyServerPacketHandler(new ArrayList());
    
    /**
     * Handles setting up and initializing common objects needed
     * in order to handle the mod's network needs. There really
     * isn't much purpose behind having multiple network channels
     * unless you are wanting to get complicated, but for most needs,
     * the default <code>HUBBY_NETWORK_CHANNEL</code> should be
     * sufficient
     */
    public static void register() {
        REGISTERED_CHANNELS.add(HUBBY_NETWORK_CHANNEL);
        REGISTERED_CHANNEL_NAMES.add(HUBBY_NETWORK_CHANNEL_NAME);
        
        // Setup networking options
        HubbyNetworkHelper.addPacketType("{C}PacketPlayerInventory", HubbyClientPacketType.PLAYER_INVENTORY);
        HubbyNetworkHelper.registerClientPacketWriter(HubbyClientPacketType.PLAYER_INVENTORY, new HubbyClientPacketWriterPlayerInventory(HubbyClientPacketType.PLAYER_INVENTORY));
        HubbyNetworkHelper.registerServerPacketProcessor(HubbyClientPacketType.PLAYER_INVENTORY, new HubbyServerPacketProcessorPlayerInventory(HubbyClientPacketType.PLAYER_INVENTORY));
        
        // setup default packet handlers for the client and server
        HUBBY_CLIENT_PACKET_HANDLER.enableChannel(HubbyNetworkHelper.getDefaultChannelName(), true);
        HUBBY_SERVER_PACKET_HANDLER.enableChannel(HubbyNetworkHelper.getDefaultChannelName(), true);
        HubbyNetworkHelper.getDefaultChannel().register(HUBBY_CLIENT_PACKET_HANDLER);  
        HubbyNetworkHelper.getDefaultChannel().register(HUBBY_SERVER_PACKET_HANDLER);
    }
        
    /**
     * Returns a list of all of the network channels that have
     * been registered. When the mod starts up, it should very
     * quickly register all channels as other commands will be
     * needing those objects soon
     * @return
     */
    public static FMLEventChannel getChannelForName(String channelName) {
        int index = REGISTERED_CHANNEL_NAMES.indexOf(channelName);
        if (index >= 0 && index < REGISTERED_CHANNEL_NAMES.size()) {
            return REGISTERED_CHANNELS.get(index);
        }
        return null;
    }
    
    /**
     * Returns the default network channel name
     * @return String - the default channel name
     */
    public static String getDefaultChannelName() {
        return HubbyNetworkHelper.HUBBY_NETWORK_CHANNEL_NAME;
    }
    
    /**
     * Returns the default network channel
     * @return FMLEventChannel - the default channel
     */
    public static FMLEventChannel getDefaultChannel() {
        return HubbyNetworkHelper.HUBBY_NETWORK_CHANNEL;
    }
    
    /**
     * Creates a new network channel that works with the
     * <code>channelName</code> provided
     * @param channelName - the channel name to register
     */
    public static void createNewChannel(String channelName) {      
        if (REGISTERED_CHANNEL_NAMES.contains(channelName)) {
            LogChannel.WARNING.log(HubbyNetworkHelper.class, "Registering network channel %s with the same name as an already existing channel!", channelName);
        }
  
        REGISTERED_CHANNEL_NAMES.add(channelName);
        REGISTERED_CHANNELS.add(NetworkRegistry.INSTANCE.newEventDrivenChannel(channelName));
    }
    
    /**
     * Registers a custom packet writer for the given packetID
     * @param packetID - the packetID
     * @param writer - the write to packet implementation
     */
    public static void registerClientPacketWriter(Enum<? extends HubbyEnumValueInterface> enumVal, HubbyClientPacketWriterInterface writer) {
        String name = HubbyNetworkHelper.getNameForPacketType(enumVal);
        HubbyNetworkHelper.CLIENT_PACKET_WRITERS.put(name, writer);
    }
    
    /**
     * Removes a writer from the registry
     * @param packetID - the packetID
     */
    public static void unregisterClientPacketWriter(Enum<? extends HubbyEnumValueInterface> enumVal) {
        String name = HubbyNetworkHelper.getNameForPacketType(enumVal);
        HubbyNetworkHelper.CLIENT_PACKET_WRITERS.remove(name);
    }
    
    /**
     * Registers a server packet processor for a specific packet type
     * @param enumVal - the packet type
     * @param processor - the processor to register
     */
    public static void registerServerPacketProcessor(Enum<? extends HubbyEnumValueInterface> enumVal, HubbyServerPacketProcessorInterface processor) {
        String name = HubbyNetworkHelper.getNameForPacketType(enumVal);
        if (HubbyNetworkHelper.SERVER_PACKET_PROCESSORS.containsKey(name)) {
            List<HubbyServerPacketProcessorInterface> processors = HubbyNetworkHelper.SERVER_PACKET_PROCESSORS.get(name);
            processors.add(processor);
        }
        else {
            ArrayList<HubbyServerPacketProcessorInterface> newProcessorList = new ArrayList<HubbyServerPacketProcessorInterface>();
            newProcessorList.add(processor);
            HubbyNetworkHelper.SERVER_PACKET_PROCESSORS.put(name, newProcessorList);
        }
    }
    
    /**
     * Removes a server packet processor based on the packet type
     */
    public static void unregisterServerPacketProcessor(Enum<? extends HubbyEnumValueInterface> enumVal) {
        String name = HubbyNetworkHelper.getNameForPacketType(enumVal);
        HubbyNetworkHelper.SERVER_PACKET_PROCESSORS.remove(name);
    }
    
    /**
     * Returns the packet writer based on the packet type
     * @return HubbyClientPacketWriterInterface - the writer for the type
     */
    public static HubbyClientPacketWriterInterface getClientWriterForPacket(Enum<? extends HubbyEnumValueInterface> packetType) {
        String name = HubbyNetworkHelper.getNameForPacketType(packetType);
        return HubbyNetworkHelper.CLIENT_PACKET_WRITERS.get(name);
    }
    
    /**
     * Returns the list of server processors for the packet type specified
     * @param enumVal - the packetType
     * @return List - the list of processors
     */
    public static List<HubbyServerPacketProcessorInterface> getServerProcessorsForPacket(Enum<? extends HubbyEnumValueInterface> packetType) {
        String name = getNameForPacketType(packetType);
        return HubbyNetworkHelper.SERVER_PACKET_PROCESSORS.get(name);
    }
    
    /**s
     * Attempts to lookup the enum value based on name
     * @param name
     * @return
     */
    public static Enum<? extends HubbyEnumValueInterface> getPacketTypeForName(String name) {
        if (PACKET_TYPES.containsKey(name)) {
            return PACKET_TYPES.get(name);
        }
        return null;
    }
    
    /**
     * Attempts to lookup the string value based on enum
     * @param enumVal
     * @return
     */
    public static String getNameForPacketType(Enum<? extends HubbyEnumValueInterface> packetType) {
        Set<String> keys = PACKET_TYPES.keySet();
        for (String key : keys) {
            if (PACKET_TYPES.get(key) == packetType) {
                return key;
            }
        }
        return null;
    }
    
    /**
     * Returns the enum value containing the underlying value passed in
     */
    public static Enum<? extends HubbyEnumValueInterface> getPacketTypeForValue(Integer value) {
        Set<String> keys = PACKET_TYPES.keySet();
        for (String key : keys) {
            HubbyEnumValueInterface packetType = (HubbyEnumValueInterface)PACKET_TYPES.get(key);
            if (packetType.getValue() == value) {
                return (Enum<? extends HubbyEnumValueInterface>) packetType;
            }
        }
        return null;
    }
    
    /**
     * Gets the enum by looking up the value in the buffer stored in the event
     * @param serverEvent - the event to fetch the enum value from
     * @return Enum - the corresponding enum value
     */
    public static Enum<? extends HubbyEnumValueInterface> getPacketTypeForServerEvent(ServerCustomPacketEvent serverEvent) {
        try {
            PacketBuffer buffer = new PacketBuffer(serverEvent.packet.payload());
            
            // We need to generate a copy of the buffer otherwise, reading values
            // from the buffer here will offset the read position when the packet is
            // actually being handled resulting in the incorrect data that can crash the game
            ByteBuf data = buffer.copy();
            PacketBuffer copyBuffer = new PacketBuffer(data);
            FMLProxyPacket copyPacket = new FMLProxyPacket(copyBuffer, serverEvent.packet.channel());
            copyPacket.readPacketData(copyBuffer);
            Integer enumValue = copyBuffer.readInt();
            return getPacketTypeForValue(enumValue);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
     
    /**
     * Adds a packet type to the map
     * @param name - the name of the packet
     * @param enumVal - the enum value for the packet
     */
    public static boolean addPacketType(String name, Enum<? extends HubbyEnumValueInterface> enumVal) {
        
        Set<String> keys = PACKET_TYPES.keySet();
        for (String key : keys) {
            HubbyEnumValueInterface mapVal = (HubbyEnumValueInterface)PACKET_TYPES.get(key);
            if (((HubbyEnumValueInterface)enumVal).getValue() == mapVal.getValue()) {
                LogChannel.WARNING.log(HubbyNetworkHelper.class, "Could not add the packet type %s; there is an existing packet type with the same value!", name);
                return false;
            }
        }
        
        PACKET_TYPES.put(name, enumVal);
        return true;
    }
    
    /**
     * Removes a packet type from the map
     * @param name - the name of the packet to remove
     */
    public static void removePacketType(String name) {
        PACKET_TYPES.remove(name);
    }
    
    /**
     * Returns if the packet is currently registered
     * @param name - the name of the packet type
     * @return boolen - is the packet registered
     */
    public static boolean isPacketTypeRegistered(String name) {
        return HubbyNetworkHelper.getPacketTypeForName(name) != null;
    }
    
    /**
     * Returns if the packet is currently registered
     * @param enumVal - the enum of the packet to check
     * @return boolean - is the packet registered
     */
    public static boolean isPacketTypeRegistered(Enum<? extends HubbyEnumValueInterface> packetType) {
        return HubbyNetworkHelper.getNameForPacketType(packetType) != null;
    }
    
    /**
     * Executes a chunk of client code and then invokes a packet 
     * to be sent along via the network with the purpose of syncing
     * the data that is on the server with the data that is on the client
     * @param packetType - the packet type being sent
     * @param channelName - the channel name being used
     * @param args - the args for the event
     * @param func - the client code to execute before sending to server
     * @return boolean - were we successful?
     */
    public static boolean executeAndSendToServer(Enum<? extends HubbyEnumValueInterface> packetType, String channelName, Callable<Map<String, Object>> func) {
        try {
            // call our client code
            Map<String, Object> args = func.call();
            
            // send the network data
            FMLEventChannel channel = HubbyNetworkHelper.getChannelForName(channelName);
            HubbyClientPacketSender.sendPacket(packetType, channelName, args);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
