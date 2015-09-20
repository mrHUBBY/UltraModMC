package com.hubby.shared.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
     * Schedule the function referenced by the callable to be
     * executed after a specified amount of time in milliseconds
     * @param func - the callable to invoke
     * @param ms - the time in milliseconds
     * @param repeats - should the call repeat
     */
    public static void schedule(final Callable func, int ms, boolean repeats) {
        
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
        
        // specify whether this scheduled action should repeat
        timer.setRepeats(repeats);
        
        // start the timer to invoke the callable after the specified time in ms
        timer.start();
    }
}
