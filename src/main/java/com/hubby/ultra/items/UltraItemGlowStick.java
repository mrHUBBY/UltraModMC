package com.hubby.ultra.items;

import java.util.ArrayList;

import com.hubby.ultra.UltraLightItemInterface;
import com.hubby.ultra.setup.UltraMod;
import com.hubby.ultra.setup.UltraRegistry;
import com.hubby.utils.HubbyConstants.LightLevel;
import com.hubby.utils.HubbyInstancedItem;
import com.hubby.utils.HubbyMath;
import com.hubby.utils.HubbyUtils;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Custom item that illuminates the blocks near the player that
 * is holding this glow stick. The battery life for these things
 * fade over time, so make sure to have enough of these in stock
 * @author davidleistiko
 */
public class UltraItemGlowStick extends HubbyInstancedItem implements UltraLightItemInterface {
    /**
     * Stores a list of <code>ItemStacks</code> that represent the various inventory
     * instances for this particular item
     */
    private static ArrayList<ItemStack> _activeGlowSticks = new ArrayList<ItemStack>();
    
    /**
     * The item name
     */
    public static final String NAME = "ultraItemGlowStick";
    
    /**
     * This property specifies the total life that a glowstick will have
     */
    public static final String PROPERTY_MAXLIFE = "PROPERTY_maxLife";
    
    /**
     * This property specifies how much life the glowstick has left before it expires
     */
    public static final String PROPERTY_REMAININGLIFE = "PROPERTY_remainingLife";
    
    /**
     * This property specifies the current light level for the glowstick
     */
    public static final String PROPERTY_LIGHTLEVEL = "PROPERTY_lightLevel";
    
    /**
     * Members
     */
    public static final LightLevel MIN_LIGHT_LEVEL = LightLevel.MIN_LIGHT_LEVEL;
    public static final LightLevel MAX_LIGHT_LEVEL = LightLevel.MAX_LIGHT_LEVEL;
    public static final Long DEFAULT_DURATION_TIME = HubbyMath.secondsToMs(2.0 * 60.0);
    
    /**
     * Constructor
     * @throws Exception 
     */
    public UltraItemGlowStick() throws Exception {
        super(UltraMod.MOD_ID, 1, UltraRegistry.ultraCreativeTab);
    }
    
    /**
     * Returns the name of the item for use
     * @return String - the item name
     */
    @Override
    public String getName() {
        return NAME;
    }
    
    /**
     * Returns the current light level for this item
     * @param stack - the <code>ItemStack</code> to get the light for
     * @return LightLevel - the light level
     */
    @Override
    public LightLevel getLightLevel(ItemStack stack) {
       
        // get the current details for this glowstick
        NBTTagCompound compound = stack.getSubCompound(getTagName(), false);
        ItemStack key = getInstanceForName(compound.getString(PROPERTY_NAME));
        if (key == null) {
            return LightLevel.INVALID;
        }
        
        // have we been recognized yet?
        compound = key.getSubCompound(getTagName(), false);
        assert compound != null : "[UltraItemGlowStick] Attempting to get the light level for an invalid itemstack instance of a glowstick!";
        
        // if we are not the glowstick that is in the
        // player's hand then we can return early
        // as we will not be giving off any light
        if (!isInstanceEnabled(key)) {
            return LightLevel.INVALID;
        }
        
        // determine the light level
        int lowVal = UltraItemGlowStick.MIN_LIGHT_LEVEL.getValue();
        int highVal = UltraItemGlowStick. MAX_LIGHT_LEVEL.getValue();
        float percent = (float)compound.getLong(PROPERTY_REMAININGLIFE) / (float)compound.getLong(PROPERTY_MAXLIFE);
        percent = HubbyMath.clamp(percent, 0.0f, 1.0f);
        int curVal = (int)Math.floor((percent * highVal) + ((1.0f - percent) * lowVal));
        
        compound.setInteger(PROPERTY_LIGHTLEVEL, LightLevel.getEnumForValue(curVal).getValue());
        key.setTagInfo(getTagName(), compound);
        return LightLevel.getEnumForValue(curVal);
    }
    
    /**
     * Returns the duration in milliseconds for the light
     * instance that is contained within the <code>ItemStack</code>
     * @return Long - the duration of the light in milliseconds
     */
    @Override 
    public Long getDuration(ItemStack stack) {
        return getInstanceProperty(stack, PROPERTY_MAXLIFE, 0L);
    }
    
    /**
     * Returns the time for which the item instance has been
     * active as contained within the <code>ItemStack</code>
     * @return Long - the active time in milliseconds
     */
    @Override 
    public Long getActiveTime(ItemStack stack) {
        Long maxLife = getInstanceProperty(stack, PROPERTY_MAXLIFE, 0L);
        Long remainingLife = getInstanceProperty(stack, PROPERTY_REMAININGLIFE, 0L);
        return maxLife - remainingLife;
    }
    
    /**
     * Denotes if the light item only works when it is the
     * currently equipped item in the player's inventory
     * @return boolean - must we be equipped?
     */
    @Override
    public boolean mustBeEquipped() {
        return true;
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
        
        if (!HubbyUtils.isClientSide(world)) {
            return;
        }
        
        // check if we have an ItemStack key with the name passed in.
        // If we don't, then we can ignore this update for now
        NBTTagCompound compound = stack.getSubCompound(getTagName(), false);
        assert compound != null : "[UltraItemGlowStick] Attempting to update an invalid itemstack instance of a glowstick!";
        ItemStack key = getInstanceForName(compound.getString(PROPERTY_NAME));
        compound = key != null ? key.getSubCompound(getTagName(), false) : null;
        if (compound == null) {
            return;
        }
        
        // Are we the item currently in the player's hand?
        if (isSelected && compound.getBoolean(PROPERTY_ENABLED)) {
            Long lifeRemaining = compound.getLong(PROPERTY_REMAININGLIFE);
            Long msDelta = HubbyUtils.getDeltaTime();
            
            lifeRemaining -= msDelta;
            lifeRemaining = Math.max(lifeRemaining, 0);
            compound.setLong(PROPERTY_REMAININGLIFE, lifeRemaining);
            
            // is this needed?
            key.setTagInfo(getTagName(), compound);
        }
    }

    /**
     * Returns if this item can be disabled/enabled
     * @return boolean - can be disabled?
     */
    @Override
    public boolean canBeDisabled() {
        return true;
    }

    /**
     * Returns the tag name used as the key for the <code>NBTTagCompound</code>
     * @return String - the tag name
     */
    @Override
    public String getTagName() {
        return "TAG_" + getName();
    }

    /**
     * Populates the <code>NBTTagCompound</code> with the data that
     * describes an instance of this item
     * @param compound - the compound to store the options
     * @param slot - the slot on the inventory of the player
     * @return NBTTagCompound - returns the compound passed in
     */
    @Override
    public NBTTagCompound initializeInstanceData(NBTTagCompound compound, Integer slot) {
        compound.setString(PROPERTY_NAME, getInstancedName());
        compound.setBoolean(PROPERTY_ENABLED, true);
        compound.setLong(PROPERTY_MAXLIFE, UltraItemGlowStick.DEFAULT_DURATION_TIME);
        compound.setLong(PROPERTY_REMAININGLIFE, UltraItemGlowStick.DEFAULT_DURATION_TIME);
        compound.setInteger(PROPERTY_SLOT, slot);
        compound.setInteger(PROPERTY_LIGHTLEVEL, UltraItemGlowStick.MAX_LIGHT_LEVEL.getValue());
        return compound;
    }

    /**
     * Returns if this light item should be considered enabled or not
     * @param stack - the instance to check
     */
    @Override
    public boolean isEnabled(ItemStack stack) {
        return isInstanceEnabled(stack);
    }

    /**
     * Returns the list of all valid properties for this item
     * @return String[] - the array of property names
     */
    @Override
    public String[] getPropertyNames() {
        return new String[] {
          PROPERTY_NAME,
          PROPERTY_ENABLED,
          PROPERTY_SLOT,
          PROPERTY_MAXLIFE,
          PROPERTY_REMAININGLIFE,
          PROPERTY_LIGHTLEVEL,
        };
    }
}
