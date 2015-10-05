package com.hubby.ultra;

import com.hubby.utils.HubbyConstants.LightLevel;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;

/**
 * This class serves as a container for a light source as well as
 * an object that has an effect on the minecraft world at its current
 * position
 * @author davidleistiko
 */
public class UltraLightSourceNode {
    
    /**
     * the actual light source item
     */
    private UltraLightSourceInterface _lightSource = null;
    
    /**
     * Keep track of where we were and where we are now
     */
    private BlockPos _prevPos = new BlockPos(0, 0 ,0);
    private BlockPos _pos = new BlockPos(0, 0, 0);
    
    /**
     * Constructor
     */
    public UltraLightSourceNode(UltraLightSourceInterface light) {
        _lightSource = light;
    }
    
    /**
     * Update function, called by the UltraLightHelper each frame
     * @return boolean - is the light still active
     */
    public boolean refresh() {
        
        // get the attached ent and make sure we are still valid
        Entity activeEnt = _lightSource.getAttachmentEntity();
        if (!validate(activeEnt)) {
            return false;
        }

        // check to see if the ent has moved, and if he has then
        // we want to update the blocks with the dynamic light value
        if (hasEntityMoved(activeEnt)) {
            updateLightLevel(_lightSource.getLightLevel());
        } 
                
        return true;
    }
    
    /**
     * Returns the current pos for this light source
     * @return BlockPos - the current pos
     */
    public BlockPos getPos() {
       return _pos;
    }
    
    /**
     * Returns the previous pos for this light container
     * @return BlockPos - the previous pos
     */
    public BlockPos getPrevPos() {
        return _prevPos;
    }
    
    /**
     * Returns the light source
     * @return UltraLightSourceInterface - the light source
     */
    public UltraLightSourceInterface getLightSource() {
        return _lightSource;
    }
    
    /**
     * Updates the light level based on the current and last positions
     * @param level - the level of the light
     */
    public void updateLightLevel(LightLevel level) {
        _lightSource.setLightLevel(level);
        _lightSource.getAttachmentEntity().worldObj.checkLightFor(EnumSkyBlock.BLOCK, _pos);
        _lightSource.getAttachmentEntity().worldObj.checkLightFor(EnumSkyBlock.BLOCK, _prevPos);
    }
    
    /**
     * Resets the block at the pos and previous pos to its default
     * value, thereby disabling the current light value
     */
    public void resetLightLevel() {
        int vanilla = UltraLightHelper.getInstance().getVanillaLightLevelForBlock(_pos);
        updateLightLevel(LightLevel.getEnumForValue(vanilla));
    }

    /**
     * Checks for the Entity coordinates to have changed.
     * Updates internal Coordinates to new position if so.
     * @return true when Entities x, y or z changed, false otherwise
     */
    private boolean hasEntityMoved(Entity ent) {
        BlockPos curPos = ent.getPosition();
        if (!curPos.equals(_pos)) {
            _prevPos = _pos;
            _pos = curPos;
            return true;
        }
        return false;
    }
    
    /**
     * Override the equals function to provide custom logic for
     * determining if one light source container is equal to another
     * @param other - the other object to compare to
     * @return boolean - the result of the compare
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof UltraLightSourceNode) {
            if (((UltraLightSourceNode)other)._lightSource == _lightSource) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Override this function to provide a custom value to be
     * used as the hash-code for this item when it is being used
     * in map structures and the like
     * @return int - the hash code
     */
    @Override
    public int hashCode() {
        return _lightSource.getAttachmentEntity().getUniqueID().hashCode();
    }
    
    /**
     * Validates that we are still good and should continue to be active
     * @param ent - the entity to check
     * @return boolean - are we still valid?
     */
    protected boolean validate(Entity ent) {
        if (ent == null || !EntityLivingBase.class.isInstance(ent)) {
            return false;
        }
        
        EntityLivingBase entLiving = (EntityLivingBase)ent;
        if (!entLiving.isEntityAlive()) {
            return false;
        }
        
        // success
        return true;
    }
}