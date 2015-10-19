package com.hubby.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.common.base.Predicate;
import com.hubby.utils.HubbyConstants.LogChannel;
import com.hubby.utils.HubbyUtils;

/**
 * This class manages a registration system where outside
 * callers can register for specific events and as a result will
 * be notified accordingly when any event corresponding to the
 * registered type is fired.
 * @author davidleistiko
 */
public class HubbyEventSender {
    
    /**
     * Simple container class that holds all items related to an
     * event listener.
     * @author davidleistiko
     */
    public class HubbyEventListenerContainer {
        /**
         * The actual listener object
         */
        public Object _listener;
        
        /**
         * The predicate which filters events so that the listener
         * will only receive notifications when the predicate is satisfied
         */
        public Predicate<HubbyEvent> _predicate;
        
        /**
         * The name of the method to invoke when an event occurs
         */
        public String _methodName;
    }
    
    /**
     * The instance
     */
    private static final HubbyEventSender INSTANCE = new HubbyEventSender();
    
    /**
     * Registered events and event listeners
     */
    private HashMap<Class<HubbyEvent>, ArrayList<HubbyEventListenerContainer>> _registration = new HashMap<Class<HubbyEvent>, ArrayList<HubbyEventListenerContainer>>();
    
    /**
     * Singleton access for the event sender
     * @return HubbyEventSender - the singleton instance
     */
    public static HubbyEventSender getInstance() {
        return INSTANCE;
    }
    
    /**
     * Registers an event listener for a specific type of event
     * @param eventType - the event type to listen for
     * @param listener - the event listener to notify when the event occurs
     * @param predicate - a predicate to evaluate in order to determine if we should notify the listener of the event (can be null)
     * @param eventMethodName - the name of the method to call when we want to notify of an event. Must point to a method with a signature matching L(HubbyEvent;)Z
     */
    public <T extends HubbyEvent> void addEventListener(Class<T> eventType, Object listener, Predicate<HubbyEvent> predicate, String eventMethodName) throws Exception {
        
        assert listener != null : "[HubbyEventSender] Invalid event listener, listener cannot be null!";
        
        HubbyEventListenerContainer container = null;
        if (_registration.containsKey(eventType)) {
            ArrayList<HubbyEventListenerContainer> listeners = _registration.get(eventType);
            container = new HubbyEventListenerContainer();
            container._listener = listener;
            container._predicate = predicate;
            container._methodName = eventMethodName;
            listeners.add(container);
        }
        else {
            ArrayList<HubbyEventListenerContainer> listeners = new ArrayList<HubbyEventListenerContainer>();
            container = new HubbyEventListenerContainer();
            container._listener = listener;
            container._predicate = predicate;
            container._methodName = eventMethodName;
            listeners.add(container);
            _registration.put((Class<HubbyEvent>) eventType, listeners);
        }
        
        // validate the most recently added event listener to make sure that the
        // method name identified actually references a valid event listener that
        // has the correct function signature and return type.
        String methodName = container._methodName;
        Class klass = container._listener.getClass();
        Method method = klass.getMethod(container._methodName, HubbyEvent.class);
        if (method == null || method.getReturnType() != boolean.class) {
            throw new Exception("The specified event listener method named as %s, does not exist or has the incorrect function signature which should be of the form (LHubbyEvent;)Z");
        }
    }
    
    /**
     * Remove the listener for the event type specified
     * @param eventType - the event to remove the listener for
     * @param listener - the listener to remove
     * @return boolean - was the listener removed?
     */
    public <T extends HubbyEvent> boolean removeEventListener(Class<T> eventType, Object listener) {
        ArrayList<HubbyEventListenerContainer> listeners = _registration.get(eventType);
        if (listeners != null) {
            Iterator<HubbyEventListenerContainer> it = listeners.iterator();
            while (it.hasNext()) {
                HubbyEventListenerContainer container = it.next();
                if (container._listener == listener) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Removes all occurrences of the event listener specified
     * @param listener - the listener to remove
     * @return Integer - how many instances were removed
     */
    public Integer removeEventListener(Object listener) {
        Integer count = 0;
        Iterator<Class<HubbyEvent>> it = _registration.keySet().iterator();
        while (it.hasNext()) {
            Class<HubbyEvent> key = it.next();
            ArrayList<HubbyEventListenerContainer> listeners = _registration.get(key);
            
            // iterate over all containers searching for the listener
            Iterator<HubbyEventListenerContainer> it2 = listeners.iterator();
            while (it2.hasNext()) {
                HubbyEventListenerContainer container = it2.next();
                if (container._listener == listener) {
                    it2.remove();
                    count += 1;
                    break;
                }
            }
        }
        
        return count;
    }
    
    /**
     * Removes all event listeners for the event type specified
     * @param eventType - the event type to remove the listeners for
     */
    public <T extends HubbyEvent> void removeEventListeners(Class<T> eventType) {
        _registration.remove(eventType);
    }
    
    /**
     * Sends out the notification for the event passed in
     * @param theEvent - the event to send
     * @return boolean - was the event handled?
     */
    public boolean notifyEvent(HubbyEvent theEvent) {
        
        LogChannel.INFO.log(HubbyEventSender.class, "Event: %s was notified at time: %s", theEvent.getName(), HubbyUtils.getCurrentDateString());
        
        Class key = theEvent.getClass();
        ArrayList<HubbyEventListenerContainer> listeners = _registration.get(key);
        for (HubbyEventListenerContainer container : listeners) {
            
            // before we fire the event, make sure that the predicate is satisfied
            // so that we only send the events that the listener is particularly
            // interested in.
            if (container._predicate == null || container._predicate.apply(theEvent)) {
                
                // No need to validate that we have a valid listener method as that was done
                // when the event listener was first registered with the event system
                try {
                    Class klass = container._listener.getClass();
                    Method method = klass.getMethod(container._methodName, HubbyEvent.class);
                    boolean result = (Boolean)method.invoke(container._listener, theEvent);
                    theEvent.setHandled(result);
                }
                catch (Exception e) {
                    LogChannel.ERROR.log(HubbyEventSender.class, "Exception thrown when attempting to invoke event listener method %s with exception %s ", container._methodName, e.getMessage());
                    theEvent.setHandled(false);
                }
                 
                // if the last listener cancelled the event then we no longer
                // need to do the propagation
                if (theEvent.isCancelled()) {
                    break;
                }
            }
        }
        
        // was the event handled?
        return theEvent.wasHandled();
    }
    
    /**
     * Sends out the notification for the event identified
     * @param eventType - the class to instantiate the event
     * @param keys - the names of the event params
     * @param args - the values of the event params
     * @return boolean - was the event handled?
     */
    public <T extends HubbyEvent> boolean notifyEvent(Class<T> eventType, String[] keys, Object[] args) {
        // create the new event
        try {
            T event = (T)eventType.newInstance();
            
            // add all event details
            if (keys != null) {
                for (int i = 0; i < keys.length; ++i) {
                    event.addEventParam(keys[i], args[i]);
                }
            }
            
            // fire the event
            return notifyEvent(event);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Notifies that an event of the type specified has occurred
     * @param eventType - the event type we care about
     * @return boolean - was the event handled?
     */
    public <T extends HubbyEvent> boolean notifyEvent(Class<T> eventType) {
        return notifyEvent(eventType, null, null);
    }
}
