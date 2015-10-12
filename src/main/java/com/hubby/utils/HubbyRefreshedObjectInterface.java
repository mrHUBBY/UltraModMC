package com.hubby.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

/**
 * Interface for any objects that which to receive a per-tick
 * callback so that they can run update code, respond to time
 * and do whatever else they need to do.
 * @author davidleistiko
 */
public abstract class HubbyRefreshedObjectInterface {

    /**
     * Constants
     */
    public static final Integer HIGHEST_PRIORITY = 0;
    public static final Integer AVERAGE_PRIORITY = 50;
    public static final Integer LOWEST_PRIORITY = 99;
    
    /**
     * The registered objects that will receive an update call each minecraft tick
     */
    private static HashMap<Integer, ArrayList<HubbyRefreshedObjectInterface>> _registeredObjects = new HashMap<Integer, ArrayList<HubbyRefreshedObjectInterface>>();

    /**
     * The elapsed amount of time stored in milliseconds. This value
     * will be initialized on the first call to the start method and then updated
     * with each successive call to the refresh method. To stop the time and the
     * refresh call the user must call stop. 
     */
    private static Long _elapsedTime = null;

    /**
     * This value stores the amount of time that has passed from the invocation
     * of the refresh method to the next time the refresh method is called. It is
     * also in milliseconds 
     */
    private static Long _deltaTime = null;
    
    /**
     * The reference time which we use to calculate the delta time which is
     * used to update the elapsed time
     */
    private static Long _startTime = null;
    
    /**
     * The value that is stored to help determine time between frames
     */
    private static Long _nextTime = null;
    
    /**
     * The timer that controls the calling of the refresh method after a specified
     * number of milliseconds
     */
    private static Timer _timer = null;

    /**
     * Determines the order in which this object will receive its refresh call
     * a value of 0 is the highest priority and these objects will be updated first.
     * If two objects have the same priority then the order in which they were
     * registered will determine which one is refreshed first
     */
    private int _priority;

    /**
     * This value is used to identify the refresh object
     */
    private String _id;

    /**
     * This is the update method that must be defined
     * in order to receive the callback
     * @param ticks
     */
    public abstract void refresh(Long delta, Long elapsed);

    /**
     * Default constructor... registers the object with
     * the updater so that it can receive its per-tick
     * callback
     */
    public HubbyRefreshedObjectInterface(String id, int priority) {
        _id = id;
        _priority = HubbyMath.clamp(priority, HIGHEST_PRIORITY, LOWEST_PRIORITY);

        if (_registeredObjects.containsKey(priority)) {
            _registeredObjects.get(_priority).add(this);
        }
        else {
            ArrayList<HubbyRefreshedObjectInterface> list = new ArrayList<HubbyRefreshedObjectInterface>();
            list.add(this);
            _registeredObjects.put(_priority, list);
        }
    }
    
    /**
     * Can we be considered as currently executing?
     * @return boolean - are we executing?
     */
    public static boolean isRunning() {
        return _timer != null;
    }

    /**
     * This method must be called in order to initialize the
     * refreshed object manager so that objects will begin to
     * receive updates and we can start tracking time.
     * @param refreshRate - the number of milliseconds that should pass between calls to <code>refresh</code>
     */
    public static void start(Long refreshRate) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-2"));
        _startTime = cal.getTimeInMillis();
        _nextTime = _startTime;
        _elapsedTime = 0L;
        _deltaTime = 0L;
        
        // if the user did not pass in a specific refresh rate then we will try to update
        // based on the TARGET_FRAME_RATE value which is 30 FPS, meaning that we should
        // roughly handle 30 refreshes each second of real time.
        long ms = (long) (refreshRate != null ? refreshRate : (1.0f / (float)HubbyConstants.TARGTE_FRAME_RATE) * 1000.0f);

        // schedule the timer to repeat after the amount of time
        // for one frame has passed
        _timer = new Timer();
        _timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                HubbyRefreshedObjectInterface.refresh();
            }
        }, 0L, ms);
    }

    /**`
     * Stops the refreshing of all registered objects and will not update
     * any until the start method is called again.
     */
    public static void stop() {
        _elapsedTime = null;
        _deltaTime = null;
        _nextTime = null;
        _startTime = null;
        
        _timer.cancel();
        _timer = null;
    }

    /**
     * Returns whether or not we are currently refreshing the
     * registered objects
     * @return boolean - are we refreshing?
     */
    public static boolean isRefreshing() {
        return _startTime != null;
    }

    /**
     * This method is to be called every Minecraft tick so that we can update
     * each registered object. The function will determine the amount of elapsed
     * time and will use that to let the objects know how much time has passed
     */
    public static void refresh() {

        // if we have not yet been started then we can simply
        // return here and no harm has been done ;)
        if (!HubbyRefreshedObjectInterface.isRefreshing()) {
            return;
        }
        
        // Determine the amount of time that has elapsed
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-2"));
        Long curTime = cal.getTimeInMillis();
        _deltaTime = curTime - _nextTime;
        _elapsedTime += _deltaTime;
        _nextTime = curTime;
       
        // first we make sure that our keys our sorted by priority so that we
        // ensure the correct objects get updated in the right order
        TreeSet<Integer> sortedKeys = new TreeSet(_registeredObjects.keySet());

        // iterate over all keys an update each object in the fetched lists
        for (Integer priority : sortedKeys) {
            List<HubbyRefreshedObjectInterface> listForPriority = _registeredObjects.get(priority);
            for (HubbyRefreshedObjectInterface obj : listForPriority) {
                obj.refresh(_deltaTime, _elapsedTime);
            }
        }
    }
    
    /**
     * Returns the elapsed time in full ticks
     * @return Integer - the number of full ticks that have elapsed
     */
    public static Integer getElapsedTicks() {
        double seconds = HubbyMath.msToSeconds(_elapsedTime);
        return HubbyMath.secondsToTicks(seconds);
    }
    
    /**
     * Returns the elapsed time in partial ticks
     * @return Double - the partial ticks for the elapsed time
     */
    public static Double getElapsedPartialTicks() {
        double seconds = HubbyMath.msToSeconds(_elapsedTime);
        return HubbyMath.secondsToPartialTicks(seconds);
    }
    
    /**
     * Returns the delta time in ticks
     * @return Double - the delta ticks
     */
    public static Integer getDeltaTicks() {
        double seconds = HubbyMath.msToSeconds(_deltaTime);
        return HubbyMath.secondsToTicks(seconds);
    }
    
    /**
     * Returns the number of partial ticks for the delta time
     * @return Double - the delta partial ticks
     */
    public static Double getDeltaPartialTicks() {
        double seconds = HubbyMath.msToSeconds(_deltaTime);
        return HubbyMath.secondsToPartialTicks(seconds);
    }
    
    /**
     * Returns the delta time in milliseconds
     * @return Long - the delta time since last frame
     */
    public static Long getDeltaTime() {
        return _deltaTime;
    }
    
    /**
     * Returns the total elapsed time since the
     * <code>start</code> method was invoked
     * @return Long - the elapsed time
     */
    public static Long getElapsedTime() {
        return _elapsedTime;
    }

    /**
     * Removes the object by finding it in its corresponding list and then
     * deletes it from there, stopping it from receiving any further updates
     * @param obj
     */
    public static void removeRefreshedObject(HubbyRefreshedObjectInterface obj) {
        List<HubbyRefreshedObjectInterface> list = _registeredObjects.get(obj._priority);
        list.remove(obj);
    }
}
