package com.hubby.events;

/**
 * Event representing when the player's inventory changes
 * @author davidleistiko
 */
public class HubbyEventPlayerInventory extends HubbyEvent {

    /**
     * Keys to use for the event params for this event
     */
    public static final String KEY_SLOT = "KeySlot";
    public static final String KEY_OLD_ITEMSTACK = "KeyOldItemStack";
    public static final String KEY_NEW_ITEMSTACK = "KeyNewItemStack";
    
    /**
     * Constructor
     * @param name - the event name
     */
    public HubbyEventPlayerInventory() {
        super("PlayerInventory");
    }
    
    /**
     * Returns the default keys that go along with this event
     * (to be overriden in subclasses)
     * @return String[] - the array of keys
     */
    public static String[] getDefaultKeySet() {
       return new String[] { KEY_SLOT, KEY_OLD_ITEMSTACK, KEY_NEW_ITEMSTACK }; 
    }
}
