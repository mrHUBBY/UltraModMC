package com.hubby.shared.utils;

/**
 * Interface for any objects that which to receive a per-tick
 * callback so that they can run update code, respond to time
 * and do whatever else they need to do.
 * @author davidleistiko
 */
public abstract class HubbyRefreshedObjectInterface {    
    /**
     * This is the update method that must be defined
     * in order to receive the callback
     * @param ticks
     */
    public abstract void refresh(int ticks);
    
    /**
     * Default constructor... registers the object with
     * the updater so that it can receive its per-tick
     * callback
     */
    public HubbyRefreshedObjectInterface() {
        
    }
}
