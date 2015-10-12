package com.hubby.ultra;

import com.hubby.utils.HubbyConstants.LightLevel;

import net.minecraft.entity.Entity;

/**
 * Simple implementation for the UltraLightSourceInterface
 * involving an entity
 * @author davidleistiko
 *
 */
public class UltraLightSourceEntity implements UltraLightSourceInterface {
    
    /**
     * Members
     */
    private Entity _entity = null;
    private LightLevel _lightLevel = LightLevel.LEVEL_8;
    
    /**
     * Constructor
     * @param entity - the entity to the attach the light to
     * @param level - the light level
     */
    public UltraLightSourceEntity(Entity entity, LightLevel level) {
        _entity = entity;
        _lightLevel = level;
    }
    
    /**
     * Returns the attached entity
     * @return Entity - the attached entity
     */
    @Override
    public Entity getAttachmentEntity() {
        return _entity;
    }

    /**
     * Gets the light level for this entity
     * @return LightLevel - the light level
     */
    @Override
    public LightLevel getLightLevel() {
        return _lightLevel;
    }

    /**
     * Sets the current light level for this entity
     * @param level - the light level
     */
    @Override
    public void setLightLevel(LightLevel level) {
        _lightLevel = level;
    }

    /**
     * Equals function
     * @param other - the other object to compare with
     * @return boolean - are they equal?
     */
    @Override
    public boolean equals(UltraLightSourceInterface other) {
        if (UltraLightSourceEntity.class.isInstance(other)) {
            UltraLightSourceEntity otherEnt = (UltraLightSourceEntity)other;
            return otherEnt._entity == this._entity && otherEnt._lightLevel == _lightLevel;
        }
        return false;
    }
}
