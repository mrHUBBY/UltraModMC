package com.hubby.ultra;

import com.hubby.utils.HubbyConstants.LightLevel;

import net.minecraft.entity.Entity;

/**
 * Describes the interface needing to be implemented in order to
 * satisfy the demands of being a light source
 * @author davidleistiko
 */
public interface UltraLightSourceInterface {

    /**
     * Returns the entity that has the light attached to it
     */
    public Entity getAttachmentEntity();
      
    /**
     * Returns the light level for this source
     * @return LightLevel - the current light level for this source
     */
    public LightLevel getLightLevel();
    
    /**
     * Changes the current light level to the new one
     * specified
     * @param level - the light level to set
     */
    public void setLightLevel(LightLevel level);
    
    /**
     * Override the equals function
     * @param other - the other to compare
     * @return boolean - the result
     */
    public boolean equals(UltraLightSourceInterface other);
}

