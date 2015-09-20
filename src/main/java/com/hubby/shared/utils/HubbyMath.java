package com.hubby.shared.utils;

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
}
