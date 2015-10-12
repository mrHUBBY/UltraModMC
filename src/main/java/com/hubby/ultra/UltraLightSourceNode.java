package com.hubby.ultra;

import com.hubby.utils.HubbyConstants;
import com.hubby.utils.HubbyConstants.LightLevel;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
    private LightLevel _initialLightLevel;
    private LightLevel _lastLightLevel;
    
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
        _initialLightLevel = light.getLightLevel();
        _lastLightLevel = light.getLightLevel();
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
        LightLevel level = determineLightLevel();
        if (level != _lastLightLevel || hasEntityMoved(activeEnt)) {
            _lastLightLevel = level;
            updateLightLevel(_lastLightLevel);
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
     * Determines what the light level should be for the entity
     * that the light is attached to.
     * @return LightLevel - the new light level that should be used
     */
    public LightLevel determineLightLevel() {
        
        // looking for the player entity so that we can calculate
        // the correct light level by also taking into account any
        // items that are in the possession of the player that also
        // give off light
        Entity ent = getLightSource().getAttachmentEntity();
        LightLevel maxItemLevel = LightLevel.MIN_LIGHT_LEVEL;
        
        // if we are the player and light items are enabled then we want
        // to calculate the new light level by taking any light items into
        // consideration
        if (EntityPlayer.class.isInstance(ent) && UltraLightHelper.getInstance().areLightItemsEnabled()) {
            EntityPlayer player = (EntityPlayer)ent;
            
            // iterate over player's main inventory looking for any light items
            // that they might have in their possession that would contribute
            // to the overall light surrounding the player
            for (int i = 0; i < HubbyConstants.HOTBAR_INVENTORY_SIZE; ++i) {
                ItemStack stack = player.inventory.mainInventory[i];
                if (stack != null && UltraLightItemInterface.class.isInstance(stack.getItem())) {
                    UltraLightItemInterface lightItem = (UltraLightItemInterface)stack.getItem();
                    if (lightItem.isEnabled(stack)) {
                        LightLevel itemLevel = lightItem.getLightLevel(stack);
                        maxItemLevel = LightLevel.max(maxItemLevel, itemLevel);
                    }
                }
            }
        }
        
        // return the max level between the value computed above with the light items
        // and the current light level as coming from the light source
        return LightLevel.max(maxItemLevel, _initialLightLevel);
    }
    
    /**
     * Resets the block at the pos and previous pos to its default
     * value, thereby disabling the current light value
     */
    public void resetLightLevel() {
        int vanilla = UltraLightHelper.getInstance().getVanillaLightValueForBlock(_pos);
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