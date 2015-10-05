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
     * Returns the light level based on the <code>ItemStack</code>
     * which acts like a unique instance of the <code>Item</code>
     * that implements this interface
     * @param stack - the <code>ItemStack</code> to get the value for
     * @return LightLevel - the light level for the stack
     */
    LightLevel getLightLevel(ItemStack stack);
}
