package com.hubby.events;

/**
 * This event is fired when the player exits the minecraft
 * world and returns to the title screen.
 * @author davidleistiko
 */
public class HubbyEventPlayerQuitsGame extends HubbyEvent {
    /**
     * Keys to use for the event params for this event
     */
    public static final String KEY_PLAYER = "KeyPlayer";

    /**
     * Constructor
     * @param name - the event name
     */
    public HubbyEventPlayerQuitsGame() {
        super("PlayerQuitsGame");
    }
    
    /**
     * Returns the default keys that go along with this event
     * (to be overriden in subclasses)
     * @return String[] - the array of keys
     */
    public static String[] getDefaultKeySet() {
       return new String[] { KEY_PLAYER }; 
    }
}