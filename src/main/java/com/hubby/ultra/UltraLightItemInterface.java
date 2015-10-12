package com.hubby.ultra;

import com.hubby.utils.HubbyConstants.LightLevel;

import net.minecraft.item.ItemStack;

/**
 * This interface describes what is needed for an item to
 * implement a light
 * @author davidleistiko
 */
public interface UltraLightItemInterface {
    
    /**
     * This value signifies a duration that never expires
     */
    public static final Long INFINITE_DURATION = -1L;
    
    /**
     *  This value signifies that the item is currently not active
     */
    public static final Long INACTIVE_TIME = -1L;
    
    /**
     * Returns the light level based on the <code>ItemStack</code>
     * which acts like a unique instance of the <code>Item</code>
     * that implements this interface
     * @param stack - the <code>ItemStack</code> to get the value for
     * @return LightLevel - the light level for the stack
     */
    LightLevel getLightLevel(ItemStack stack);
    
    /**
     * Specifies if the light item only gives off light if it
     * is the currently equipped item in the player's inventory
     * @return boolean - does the item need to be the selected item?
     */
    boolean mustBeEquipped();
    
    /**
     * How long does this thing generate light for
     * @param stack - the instance to check the duration for
     * @return Long - the time in milliseconds (returns <code>INFINITE_DURATION</code> if infinite)
     */
    Long getDuration(ItemStack stack);
    
    /**
     * Get the current time that this has been active for
     * @param stack - the instance to get the active time for
     * @return Long - the active time in milliseconds (returns <code>INACTIVE_TIME</code> when not active)
     */
    Long getActiveTime(ItemStack stack);
    
    /**
     * Is the light instance currently enabled
     * @param stack - the instance to check
     * @return boolean - is this instance enabled?
     */
    boolean isEnabled(ItemStack stack);
}
