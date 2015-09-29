package com.hubby.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.swing.Timer;

/**
 * The purpose of this class is to provide a convenience for callers
 * to schedule a function to be invoked at a later time 
 * @author davidleistiko
 *
 */
public class HubbyScheduler {
    
    /**
     * The list of all timers that have been scheduled
     */
    private static final Map<String, Timer> TIMERS = new HashMap<String, Timer>();
    
    /**
     * Schedule the function referenced by the callable to be
     * executed after a specified amount of time in milliseconds
     * @param func - the callable to invoke
     * @param ms - the time in milliseconds
     * @param repeats - should the call repeat
     */
    public static void schedule(String id, final Callable func, int ms, boolean repeats) {
        
        // create the swing timer
        Timer timer = new Timer(ms, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    func.call();
                }
                catch (Exception e) {
                    System.out.println("[HubbyScheduler] When attempting to invoke a callback an unhandled exception was thrown!");
                    e.printStackTrace();
                }
            }
        });
        
        // add the new timer to our collection for bookkeeping
        TIMERS.put(id, timer);
        
        // specify whether this scheduled action should repeat
        timer.setRepeats(repeats);
        
        // start the timer to invoke the callable after the specified time in ms
        timer.start();
    }
    
    /**
     * This method checks if the timer is currently running
     * @param id - the id of the Timer to lookup
     * @return boolean - is the Timer running? (return false for invalid Timer id's)
     */
    public static boolean isRunning(String id) {
        if (TIMERS.containsKey(id)) {
            return TIMERS.get(id).isRunning();
        }
        return false;
    }
    
    /**
     * Stops the Timer by id
     * @param id - the id of the Timer to stop
     * @return boolean - did we actually stop a Timer?
     */
    public static boolean stop(String id) {
        if (TIMERS.containsKey(id)) {
            TIMERS.get(id).stop();
            return true;
        }
        return false;
    }
    
    /**
     * Attempts to restart the Timer by id
     * @param id - the id of the Timer to restart
     * @return boolean - did we successfully restart the Timer?
     */
    public static boolean restart(String id) {
        if (TIMERS.containsKey(id)) {
            TIMERS.get(id).restart();
            return true;
        }
        return false;
     }
    
    /**
     * Returns if the timer is currently set to repeat
     * @param id - the id of the Timer to check
     * @return boolean - is the Timer repeating? (returns false for invalid id's)
     */
    public static boolean isRepeating(String id) {
        if (TIMERS.containsKey(id)) {
            return TIMERS.get(id).isRepeats();
        }
        return false;
    }
    
    /**
     * Sets the identified Timer to either repeat or not
     * @param id - the id of the Timer to lookup
     * @param repeats - should the Timer repeat?
     * @return boolean - did we actually update a Timer?
     */
    public static boolean setRepeating(String id, boolean repeats) {
        if (TIMERS.containsKey(id)) {
            TIMERS.get(id).setRepeats(repeats);
            return true;
        }
        return false;
    }
}
