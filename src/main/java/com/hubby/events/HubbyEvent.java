package com.hubby.events;

import java.util.HashMap;

/**
 * Base class for all events fired
 * @author davidleistiko
 */
public class HubbyEvent {
    
    /**
     * The name of the event
     */
    protected String _name = "";
    
    /**
     * The params passed along with the event
     */
    protected HashMap<String, Object> _params = new HashMap<String, Object>();
    
    /**
     * Should this event continue to propagate to other
     * event listeners?
     */
    protected boolean _cancelled = false;
    
    /**
     * Was the event successfully handled?
     */
    protected boolean _handled = false;
    
    /**
     * Constructor
     * @param name - the name of the event
     */
    public HubbyEvent(String name) {   
    }
    
    /**
     * Returns the default keys that go along with this event
     * (to be overriden in subclasses)
     * @return String[] - the array of keys
     */
    public static String[] getDefaultKeySet() {
       return new String[] {}; 
    }
    
    /**
     * Returns the parameter for the given name if the value is of the
     * matching class type
     * @param name
     * @param klass
     * @return
     */
    public <T extends Object> T getEventParam(String name, Class<T> klass) {
        Object val = _params.get(name);
        if (klass.isInstance(val)) {
            return (T)val;
        }
        return null;
    }
    
    /**
     * Adds a named parameter to the params collection
     * @param name - the name of the param
     * @param val - the value of the param
     */
    public void addEventParam(String name, Object val) {
        _params.put(name, val);
    }
    
    /**
     * Removes an event param by name (if it exists)
     * @param name - the name of the param to remove
     * @return boolean - was the param removed?
     */
    public boolean removeEventParam(String name) {
        return _params.remove(name) != null;
    }
    
    /**
     * Access for the event name
     * @return String - the event name
     */
    public String getName() {
        return _name;
    }
    
    /**
     * Sets this event cancelled or not
     * @param cancel - should we cancel the event
     */
    public void setCancelled(boolean cancel) {
        _cancelled = cancel;
    }
    
    /**
     * Are we currently cancelled?
     * @return boolean - cancelled?
     */
    public boolean isCancelled() {
        return _cancelled;
    }
    
    /**
     * Marks that this event was handled or not
     * @param handled - handled ?
     */
    public void setHandled(boolean handled) {
        // When an event is mark as being handled, the handled flag will
        // remain as true even if subsequent handlers attempt to mark it
        // as false
        _handled |= handled;
    }
    
    /**
     * Was this event handled by an event listener?
     * @return boolean - was handled?
     */
    public boolean wasHandled() {
        return _handled;
    }
}
