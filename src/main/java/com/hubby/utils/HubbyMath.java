package com.hubby.utils;

/**
 * A collection of useful math operations that make
 * life much easier
 * @author davidleistiko
 */
public class HubbyMath {
    /**
     * Clamps the value between the min and max values
     * @param value - the value to clamp
     * @param min - the min allowed value
     * @param max - the max allowed value
     * @return int - the clamped value
     */
    public static int clamp(int value, int min, int max) {
        return Math.min(max, Math.max(value, min));
    }

    /**
     * Clamps the value between the min and max values
     * @param value - the value to clamp
     * @param min - the min allowed value
     * @param max - the max allowed value
     * @return float - the clamped value
     */
    public static float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }

    /**
     * Clamps the value between the min and max values
     * @param value - the value to clamp
     * @param min - the min allowed value
     * @param max - the max allowed value
     * @return double - the clamped value
     */
    public static float clamp(double value, double min, double max) {
        return Math.min((float) max, Math.max((float) value, (float) min));
    }
    
    /**
     * Check if a value is within the range provided
     * @param value - the value to check
     * @param min - the least most possible value
     * @param max - the max most possible value
     * @return boolean - whether the value was within the range or not
     */
    public static boolean isWithinRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Check if a value is within the range provided
     * @param value - the value to check
     * @param min - the least most possible value
     * @param max - the max most possible value
     * @return boolean - whether the value was within the range or not
     */
    public static boolean isWithinRange(float value, float min, float max) {
        return value >= min && value <= max;
    }
    
    /**
     * Convert long milliseconds to double seconds
     * @param ms - the number of milliseconds
     * @return double - the converted number of seconds
     */
    public static double msToSeconds(Long ms) {
        double msd = (double)ms;
        return msd / 1000.0D;
    }
    
    /**
     * Converts seconds to milliseconds
     * @param secs - the seconds to convert
     * @return Long - the converted value
     */
    public static Long secondsToMs(Double secs) {
        double ms = Math.floor(secs * 1000.0);
        return (long)ms;
    }
    
    /**
     * Returns the full ticks that have elasped based on the
     * number of seconds passed in
     * @param seconds
     * @return
     */
    public static Integer secondsToTicks(double seconds) {
        return (int)Math.floor(seconds * HubbyConstants.TICKS_PER_SECOND);
    }
    
    /**
     * Returns the partial ticks which is the value after
     * the decimal point for the number of seconds that have elapsed
     * @param seconds - the number of seconds to convert
     * @return
     */
    public static Double secondsToPartialTicks(double seconds) {
        double ticks = seconds * HubbyConstants.TICKS_PER_SECOND;
        return ticks - Math.floor(ticks);
    }
    
    /**
     * Converts degrees to radians
     * @param degrees - the degrees to convert
     * @return double - the radians
     */
    public static double toRadians(double degrees) {
        return Math.PI * degrees / 180.0;
    }
    
    /**
     * Converts radians to degrees
     * @param radians - the radians to convert
     * @return double - the degrees
     */
    public static double toDegrees(double radians) {
        return radians * 180.0 / Math.PI;
    }
}

