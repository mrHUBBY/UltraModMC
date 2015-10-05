package com.hubby.ultra;

import com.hubby.utils.HubbyConstants.LightLevel;

/**
 * This interface describes what is needed for an item to
 * implement a light
 * @author davidleistiko
 */
public interface UltraLightItemInterface {
    /**
     * Returns the current light level should be between 0 and 15
     */
    LightLevel getLightLevel();
}
