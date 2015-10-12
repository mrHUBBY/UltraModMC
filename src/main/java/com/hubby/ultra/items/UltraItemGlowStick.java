package com.hubby.ultra.items;

import java.util.HashMap;

import com.hubby.ultra.UltraLightItemInterface;
import com.hubby.ultra.setup.UltraMod;
import com.hubby.ultra.setup.UltraRegistry;
import com.hubby.utils.HubbyConstants.LightLevel;
import com.hubby.utils.HubbyMath;
import com.hubby.utils.HubbyNamedObjectInterface;
import com.hubby.utils.HubbyUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Custom item that illuminates the blocks near the player that
 * is holding this glow stick. The battery life for these things
 * fade over time, so make sure to have enough of these in stock
 * @author davidleistiko
 */
public class UltraItemGlowStick extends Item implements UltraLightItemInterface, HubbyNamedObjectInterface {

    /**
     * Details class that stores more information about each glowstick
     * that is in the player's possession
     * @author davidleistiko
     */
    public class Details {
        /**
         * The maximum life duration for this light item
         */
        public Long _maxLife = UltraItemGlowStick.DEFAULT_DURATION_TIME;
        
        /**
         * How much time in milliseconds remains before this item reaches its
         * minimum light level
         */
        public Long _remainingLife = UltraItemGlowStick.DEFAULT_DURATION_TIME;
        
        /**
         * The current light level for this glowstick
         */
        public LightLevel _lightLevel = UltraItemGlowStick.MIN_LIGHT_LEVEL;
        
        /**
         * Is this glowstick enabled currently? This can be false even
         * when there is remaining life for this glowstick
         */
        public boolean _enabled = true;
        
        /**
         * The slot at which this glowstick resides
         */
        public Integer _slot = 0;
        
        /**
         * Constructor
         */
        public Details() {   
        }
    }
    
    /**
     * This map keeps track of the various glowsticks the player may have
     * in their inventory and updates each one accordingly so that as the life
     * of the glowstick progresses its light level, oppositely, decreases.
     */
    private static HashMap<ItemStack, Details> _glowstickDetailsMap = new HashMap<ItemStack, Details>();
    
    /**
     * Members
     */
    public static final LightLevel MIN_LIGHT_LEVEL = LightLevel.MIN_LIGHT_LEVEL;
    public static final LightLevel MAX_LIGHT_LEVEL = LightLevel.MAX_LIGHT_LEVEL;
    public static final Long DEFAULT_DURATION_TIME = HubbyMath.secondsToMs(2.0 * 60.0);
    
    /**
     * The item name
     */
    public static final String NAME = "ultraItemGlowStick";
    
    /**
     * Constructor
     */
    public UltraItemGlowStick() {
        this.setUnlocalizedName(NAME);
        this.setCreativeTab(UltraRegistry.ultraCreativeTab);
        
        // actually register the item with forge and mc for rendering
        HubbyUtils.registerNamedItem(UltraMod.MOD_ID, this);
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
        Details details = _glowstickDetailsMap.get(stack);
        
        // have we been recognized yet?
        if (details == null) {
            return LightLevel.INVALID;
        }
        
        // if we are not the glowstick that is in the
        // player's hand then we can return early
        // as we will not be giving off any light
        if ((mustBeEquipped() && !HubbyUtils.isEquippedItem(stack)) || !isEnabled(stack)) {
            return LightLevel.INVALID;
        }
        
        // determine the light level
        int lowVal = UltraItemGlowStick.MIN_LIGHT_LEVEL.getValue();
        int highVal = UltraItemGlowStick. MAX_LIGHT_LEVEL.getValue();
        float percent = (float)details._remainingLife / (float)details._maxLife;
        percent = HubbyMath.clamp(percent, 0.0f, 1.0f);
        int curVal = (int)Math.floor((percent * highVal) + ((1.0f - percent) * lowVal));
        
        details._lightLevel = LightLevel.getEnumForValue(curVal);
        _glowstickDetailsMap.put(stack, details);
        return details._lightLevel;
    }
    
    /**
     * Returns the duration in milliseconds for the light
     * instance that is contained within the <code>ItemStack</code>
     * @return Long - the duration of the light in milliseconds
     */
    @Override 
    public Long getDuration(ItemStack stack) {
        Details details = _glowstickDetailsMap.get(stack);
        if (details == null) {
            return 0L;
        }
        return details._maxLife;
    }
    
    /**
     * Returns the time for which the item instance has been
     * active as contained within the <code>ItemStack</code>
     * @return Long - the active time in milliseconds
     */
    @Override 
    public Long getActiveTime(ItemStack stack) {
        Details details = _glowstickDetailsMap.get(stack);
        if (details == null) {
            return 0L;
        }
        return details._maxLife - details._remainingLife;
    }
    
    /**
     * Is this light instance currently enabled?
     * @param stack - the item instance
     * @return boolean - is enabled?
     */
    @Override
    public boolean isEnabled(ItemStack stack) {
        Details details = _glowstickDetailsMap.get(stack);
        if (details == null) {
            return false;
        }
        if (!mustBeEquipped() || HubbyUtils.isEquippedItem(stack)) {
            return details._enabled && details._remainingLife > 0L;
        }
        return false;
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
        
        // Is this the first time that the player has equipped us?
        // If so, then we want to track this ItemStack as an instance
        // of the glowstick light item by adding it to the map and 
        // then tracking the time that the item is active (the time
        // that the item is in the player's hand)
        if (!_glowstickDetailsMap.containsKey(stack)) {
            Details details = new Details();
            details._slot = itemSlot;
            _glowstickDetailsMap.put(stack, new Details());
            return;
        }
        
        // Are we the item currently in the player's hand?
        Details details = _glowstickDetailsMap.get(stack);
        details._slot = itemSlot;
        _glowstickDetailsMap.put(stack, details);
        
        if (isSelected && details._enabled) {
            Long lifeRemaining = details._remainingLife;
            Long msDelta = HubbyUtils.getDeltaTime();
            
            lifeRemaining -= msDelta;
            lifeRemaining = Math.max(lifeRemaining, 0);
            details._remainingLife = lifeRemaining;
            _glowstickDetailsMap.put(stack, details);
        }
    }
    
    /**
     * Handle when the player uses the right-click for this item
     * @param itemStack - the item stack containing this item
     * @param world - the world
     * @param player - the player who performed the right click
     */
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (HubbyUtils.isClientSide(world)) {
            Details details = _glowstickDetailsMap.get(itemStack);
            if (details != null) {
                details._enabled = !details._enabled;
                _glowstickDetailsMap.put(itemStack, details);
            }
        }
        
        //this.setHasSubtypes(true);
        
        //this.getSubItems(itemIn, tab, subItems);
        
        return itemStack;
    }
//    
//    /**
//     * An array of 36 item stacks indicating the main player inventory (including the visible bar).
//     */
//    public ItemStack[] mainInventory = new ItemStack[36];
//
//    /** An array of 4 item stacks containing the currently worn armor pieces. */
//    public ItemStack[] armorInventory = new ItemStack[4];
//    /**
//     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
//     */
//    public void setInventorySlotContents(int index, ItemStack stack)
//    {
//        ItemStack[] var3 = this.mainInventory;
//
//        if (index >= var3.length)
//        {
//            index -= var3.length;
//            var3 = this.armorInventory;
//            UltraUtils.onPlayerInventoryArmorChanged(index, var3[index], stack);
//        }
//        else {
//            UltraUtils.onPlayerInventorySlotContentsChanged(index, var3[index], stack);
//        }
//
//        var3[index] = stack;
//    }
}
