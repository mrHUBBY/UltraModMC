package com.hubby.ultra.items;

import java.util.HashMap;

import com.hubby.ultra.UltraLightItemInterface;
import com.hubby.utils.HubbyConstants.LightLevel;
import com.hubby.utils.HubbyMath;
import com.hubby.utils.HubbyRefreshedObjectInterface;
import com.hubby.utils.HubbyUtils;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Custom item that illuminates the blocks near the player that
 * is holding this glow stick. The battery life for these things
 * fade over time, so make sure to have enough of these in stock
 * @author davidleistiko
 */
public class UltraItemGlowStick extends Item implements UltraLightItemInterface {

    /**
     * This map keeps track of the various glowsticks the player may have
     * in their inventory and updates each one accordingly so that as the life
     * of the glowstick progresses its light level, oppositely, decreases.
     */
    private static HashMap<ItemStack, Long> _lifetimeDurationMap = new HashMap<ItemStack, Long>();
    
    /**
     * Members
     */
    public static final LightLevel MIN_LIGHT_LEVEL = LightLevel.MIN_LIGHT_LEVEL;
    public static final LightLevel MAX_LIGHT_LEVEL = LightLevel.MAX_LIGHT_LEVEL;
    
    /**
     * The maximum lifetime value for all glowsticks given in milliseconds
     */
    public static final Long MAX_LIFETIME = HubbyMath.secondsToMs(60.0 * 3.0);

    /**
     * Returns the current light level for this item
     * @return LightLevel - the light level
     */
    @Override
    public LightLevel getLightLevel(ItemStack stack) {
       
        // this itemstack is not tracked?
        if (!_lifetimeDurationMap.containsKey(stack)) {
            return LightLevel.INVALID;
        }
        
        // if we are currently not the equipped item
        // then we need to bail as no light value should
        // be emitted unless we are the active item
        if (!HubbyUtils.isEquippedItem(stack)) {
            return LightLevel.INVALID;
        }
        
        // determine the light level
        int lowVal = UltraItemGlowStick.MIN_LIGHT_LEVEL.getValue();
        int highVal = UltraItemGlowStick. MAX_LIGHT_LEVEL.getValue();
        Long lifeRemaining = _lifetimeDurationMap.get(stack);
        float percent = (float)lifeRemaining / (float)MAX_LIFETIME;
        percent = HubbyMath.clamp(percent, 0.0f, 1.0f);
        int curVal = (int)Math.floor((percent * highVal) + ((1.0f - percent) * lowVal));
        return LightLevel.getEnumForValue(curVal);
    }
    
    /**
     * Update method called as long as the item is currently equipped within
     * the player's inventory
     * @param stack - the stack containing this item
     * @param world - the world
     * @param ent - the entity that possesses the item
     * @param itemSlot - the slot within the inventory
     * @param isSelected - are we the player's currently equipped item?
     */
    @Override
    public void onUpdate(ItemStack stack, World world, Entity ent, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, ent, itemSlot, isSelected);
        
        // Is this the first time that the player has equipped us?
        // If so, then we want to track this ItemStack as an instance
        // of the glowstick light item by adding it to the map and 
        // then tracking the time that the item is active (the time
        // that the item is in the player's hand)
        if (!_lifetimeDurationMap.containsKey(stack)) {
            _lifetimeDurationMap.put(stack, MAX_LIFETIME);
            return;
        }
        
        // Are we the item currently in the player's hand?
        if (isSelected) {
            Long lifeRemaining = _lifetimeDurationMap.get(stack);
            Long msDelta = HubbyRefreshedObjectInterface.getDeltaTime();
            
            lifeRemaining -= msDelta;
            lifeRemaining = Math.max(lifeRemaining, 0);
            _lifetimeDurationMap.put(stack, lifeRemaining);
        }
    }
}
