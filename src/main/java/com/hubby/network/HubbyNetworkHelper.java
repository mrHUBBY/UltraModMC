package com.hubby.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.hubby.utils.HubbyConstants;
import com.hubby.utils.HubbyEnumValueInterface;

import net.minecraft.network.PacketBuffer;

/**
 * Simple helper class for network functionality
 */
public class HubbyNetworkHelper {

    /**
     * Simple interface that specifies what it should look like
     * when we want to write to a buffer to send along in a packet
     * @author davidleistiko
     *
     */
    public interface HubbyClientPacketWriterInterface {
        void writeToBuffer(PacketBuffer buffer, Map<String, Object> args); 
    }
    
    /**
     * This map stores the relationship between a packet name and a
     * corresponding enum value
     */
    private static final Map<String, Enum<? extends HubbyEnumValueInterface>> PACKET_TYPES = new HashMap<String, Enum<? extends HubbyEnumValueInterface>>();
    
    /**
     * Stores registered packet writers
     */
    private static final Map<String, HubbyClientPacketWriterInterface> PACKET_WRITERS = new HashMap<String, HubbyClientPacketWriterInterface>();
    
    /**
     * Registers a custom packet writer for the given packetID
     * @param packetID - the packetID
     * @param writer - the write to packet implementation
     */
    public static void registerPacketWriter(Enum<? extends HubbyEnumValueInterface> enumVal, HubbyClientPacketWriterInterface writer) {
        String name = HubbyNetworkHelper.getNameForEnum(enumVal);
        HubbyNetworkHelper.PACKET_WRITERS.put(name, writer);
    }
    
    /**
     * Removes a writer from the registry
     * @param packetID - the packetID
     */
    public static void unregisterPacketWriter(Enum<? extends HubbyEnumValueInterface> enumVal) {
        String name = HubbyNetworkHelper.getNameForEnum(enumVal);
        HubbyNetworkHelper.PACKET_WRITERS.remove(name);
    }
    
    /**
     * Returns the packet writer based on the packet type
     * @return HubbyClientPacketWriterInterface - the writer for the type
     */
    public static HubbyClientPacketWriterInterface getPacketWriterForPacket(Enum<? extends HubbyEnumValueInterface> enumVal) {
        String name = HubbyNetworkHelper.getNameForEnum(enumVal);
        return HubbyNetworkHelper.PACKET_WRITERS.get(name);
    }
    
    /**
     * Attempts to lookup the enum value based on name
     * @param name
     * @return
     */
    public static Enum<? extends HubbyEnumValueInterface> getEnumForName(String name) {
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
    public static String getNameForEnum(Enum<? extends HubbyEnumValueInterface> enumVal) {
        Set<String> keys = PACKET_TYPES.keySet();
        for (String key : keys) {
            if (PACKET_TYPES.get(key) == enumVal) {
                return key;
            }
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
                HubbyConstants.LogChannel.WARNING.log(HubbyNetworkHelper.class, "Could not add the packet type %s; there is an existing packet type with the same value!", name);
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
        return HubbyNetworkHelper.getEnumForName(name) != null;
    }
    
    /**
     * Returns if the packet is currently registered
     * @param enumVal - the enum of the packet to check
     * @return boolean - is the packet registered
     */
    public static boolean isPacketTypeRegistered(Enum<? extends HubbyEnumValueInterface> enumVal) {
        return HubbyNetworkHelper.getNameForEnum(enumVal) != null;
    }
}
