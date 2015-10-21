package com.hubby.events;

/**
 * This event is to capture when the player first joins the
 * game through interacting with the main menu flow
 * @author davidleistiko
 */
public class HubbyEventPlayerJoinsGame extends HubbyEvent {
    /**
     * Keys to use for the event params for this event
     */
    public static final String KEY_PLAYER = "KeyPlayer";

    /**
     * Constructor
     * @param name - the event name
     */
    public HubbyEventPlayerJoinsGame() {
        super("PlayerJoinsGame");
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
