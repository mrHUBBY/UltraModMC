package com.hubby.utils;

import java.util.ArrayList;

import com.google.common.base.Predicate;
import com.hubby.events.HubbyEvent;
import com.hubby.events.HubbyEventPlayerInventory;
import com.hubby.events.HubbyEventPlayerQuitsGame;
import com.hubby.events.HubbyEventSender;
import com.hubby.utils.HubbyConstants.LogChannel;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

/**
 * Provides an interface for any item that can be instanced with
 * the ability to store unique information per instance and will
 * track their lifetime.
 * @author davidleistiko
 */
public abstract class HubbyInstancedItem extends Item implements HubbyNamedObjectInterface {

    /**
     * The list that keeps the references to all of the instances of
     * this particular item
     */
    private static final ArrayList<ItemStack> INSTANCES = new ArrayList<ItemStack>();

    /**
     * The constant property for looking up the name of an 
     * item in the <code>NBTTagCompound</code>
     */
    public static final String PROPERTY_NAME = "PROPERTY_name";

    /**
     * The constant property for looking up whether or not an
     * instance of this item is enabled or not
     */
    public static final String PROPERTY_ENABLED = "PROPERTY_enabled";

    /**
     * The constant property for looking up the inventory
     * slot position for this item
     */
    public static final String PROPERTY_SLOT = "PROPERTY_slot";

    /**
     * Used for building instanced names
     */
    private Integer _instancedNameCounter = 0;

    /**
     * How many instances of this particular item do we allow the player to have?
     */
    private Integer _instanceLimit = 1;

    /**
     * Constructor
     * @throws Exception 
     */
    public HubbyInstancedItem(String modID, Integer maxStackSize, CreativeTabs tab) throws Exception {
        this.setUnlocalizedName(getName());
        this.setCreativeTab(tab);
        this.setMaxStackSize(maxStackSize);

        // actually register the item with forge and mc for rendering
        HubbyUtils.registerNamedItem(modID, this);

        // store a reference to ourselves so that we can use it
        // in the predicate below
        final HubbyInstancedItem thisItem = this;

        // register for player inventory changed events
        Predicate<HubbyEvent> pred = new Predicate<HubbyEvent>() {
            @Override
            public boolean apply(HubbyEvent event) {
                ItemStack newStack = event.getEventParam(HubbyEventPlayerInventory.KEY_NEW_ITEM_STACK, ItemStack.class);
                ItemStack oldStack = event.getEventParam(HubbyEventPlayerInventory.KEY_OLD_ITEM_STACK, ItemStack.class);
                return (newStack != null && newStack.getItem() == thisItem) || (oldStack != null && oldStack.getItem() == thisItem);
            }
        };

        // Let's register for all 'PlayerInventory' events so that we can catch when any ItemStacks
        // are created, updated and/or removed from within the player's main inventory
        HubbyEventSender.getInstance().addEventListener(HubbyEventPlayerInventory.class, this, pred, "handleInventoryEvent");
        HubbyEventSender.getInstance().addEventListener(HubbyEventPlayerQuitsGame.class, this, null, "handlePlayerQuitsGameEvent");
    }

    /**
     * Sets the new instance limit for this item (a value less than 0 indicates an infinite limit)
     * @param limit - the number of allowed instances
     */
    public void setInstanceLimit(Integer limit) {
        _instanceLimit = limit;
    }

    /**
     * Returns the number of allowed instances
     * @return Integer - allowed instance count
     */
    public Integer getInstanceLimit() {
        return _instanceLimit;
    }
    
    /**
     * Returns the current number of instances for this
     * particular item
     * @return Integer - the number of instances
     */
    public Integer getInstanceCount() {
        return INSTANCES.size();
    }
    
    /**
     * Returns whether or not we can have as many instances of this
     * item as we would like.
     * @return boolean - has infinite limit?
     */
    public boolean hasInfiniteLimit() {
        return getInstanceLimit() < 0;
    }
    
    /**
     * Returns if we are maxed out with the number of instances
     * that we are allowed to have
     * @return
     */
    public boolean hasReachedInstanceLimit() {
        return getInstanceCount() >= getInstanceLimit() && !hasInfiniteLimit();
    }

    /**
     * Checks if the instanced item is actually the equipped item
     * for the player currently
     * @param stack - the instance to check
     * @return boolean - are we equipped?
     */
    public boolean isInstanceEquipped(ItemStack stack) {
        Integer equippedSlot = HubbyUtils.getPlayersCurrentlyEquippedItemSlot();
        NBTTagCompound compound = stack.getSubCompound(getTagName(), false);
        if (compound != null) {
            return compound.getInteger(PROPERTY_SLOT) == equippedSlot;
        }
        return false;
    }

    /**
     * Is this light instance currently enabled?
     * @param stack - the item instance
     * @return boolean - is enabled?
     */
    public boolean isInstanceEnabled(ItemStack stack) {
        if (!canBeDisabled()) {
            return true;
        }

        NBTTagCompound compound = stack.getSubCompound(getTagName(), false);
        ItemStack key = getInstanceForName(compound.getString(PROPERTY_NAME));
        compound = key != null ? key.getSubCompound(getTagName(), false) : null;
        if (compound == null) {
            return false;
        }

        if (!mustBeEquipped() || isInstanceEquipped(key)) {
            return compound.getBoolean(PROPERTY_ENABLED);
        }
        return false;
    }

    /**
     * Returns the instance property
     * @param stack - the instance of the item
     * @param property - the property to lookup
     * @return T - the value of the property
     */
    public <T extends Object> T getInstanceProperty(ItemStack stack, String property) {
        return getInstanceProperty(stack, property, null);
    }

    /**
     * Returns a property retrieved from the instanced data
     * @param stack - the instanced item
     * @param property - the name of the property we are wanting to retrieve
     * @param defaultVal - the default value to return if we can't find the property
     * @return T - the value of the property (defaultVal if property does not exist)
     */
    public <T extends Object> T getInstanceProperty(ItemStack stack, String property, T defaultVal) {
        NBTTagCompound compound = stack.getSubCompound(getTagName(), false);
        ItemStack key = getInstanceForName(compound.getString(PROPERTY_NAME));
        compound = key != null ? key.getSubCompound(getTagName(), false) : null;
        if (compound == null) {
            return defaultVal;
        }

        Class klass = defaultVal.getClass();
        if (klass.isAssignableFrom(Integer.class) || klass.isAssignableFrom(int.class)) {
            return (T) ((Integer) compound.getInteger(property));
        }
        else if (klass.isAssignableFrom(Float.class) || klass.isAssignableFrom(float.class)) {
            return (T) ((Float) compound.getFloat(property));
        }
        else if (klass.isAssignableFrom(Double.class) || klass.isAssignableFrom(double.class)) {
            return (T) ((Double) compound.getDouble(property));
        }
        else if (klass.isAssignableFrom(String.class) || klass.isAssignableFrom(char[].class)) {
            return (T) compound.getString(property);
        }
        else if (klass.isAssignableFrom(Byte.class) || klass.isAssignableFrom(byte.class)) {
            return (T) ((Byte) compound.getByte(property));
        }
        else if (klass.isAssignableFrom(Short.class) || klass.isAssignableFrom(short.class)) {
            return (T) ((Short) compound.getShort(property));
        }
        else if (klass.isAssignableFrom(Long.class) || klass.isAssignableFrom(long.class)) {
            return (T) ((Long) compound.getLong(property));
        }
        else if (klass.isAssignableFrom(Boolean.class) || klass.isAssignableFrom(boolean.class)) {
            return (T) ((Boolean) compound.getBoolean(property));
        }
        else if (klass.isAssignableFrom(Integer[].class) || klass.isAssignableFrom(int[].class)) {
            return (T) compound.getIntArray(property);
        }
        else if (klass.isAssignableFrom(Byte[].class) || klass.isAssignableFrom(byte[].class)) {
            return (T) compound.getByteArray(property);
        }
        else if (klass.isAssignableFrom(NBTTagList.class)) {
            return (T) compound.getTagList(property, compound.getTagType(property));
        }
        else if (klass.isAssignableFrom(NBTTagCompound.class)) {
            return (T) compound.getCompoundTag(property);
        }
        else if (klass.isAssignableFrom(NBTBase.class)) {
            return (T) compound.getTag(property);
        }

        LogChannel.WARNING.log(HubbyInstancedItem.class, "Invalid property type (%s) specified when attempting to lookup instanced property", klass.getName());
        return defaultVal;
    }

    /**
     * Handle when the user right-clicks while this is the equipped item
     * @param itemStack - the item that was clicked
     * @param world - the current world this took place in
     * @param player - the player who did the action
     * @return ItemStack - the resulting <code>ItemStack</code>
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (HubbyUtils.isClientSide(world)) {
            NBTTagCompound compound = itemStack.getSubCompound(getTagName(), false);
            assert compound != null : "Attempting to right-click an invalid itemstack instanced item!";
            ItemStack key = getInstanceForName(compound.getString(PROPERTY_NAME));
            if (key != null) {
                compound = key.getSubCompound(getTagName(), false);
                assert compound != null : "Attempting to right-click an invalid itemstack instanced item!";

                // only toggle the enable flag if we have the permission to
                if (canBeDisabled()) {
                    compound.setBoolean(PROPERTY_ENABLED, !compound.getBoolean(PROPERTY_ENABLED));
                    key.setTagInfo(getTagName(), compound);
                }
            }
        }

        // return the new ItemStack
        return itemStack;
    }
    
    public boolean containsItemInstance(ItemStack stack) {
        return INSTANCES.contains(stack);
    }

    /**
     * Event notification callback mechanism. Responds to the
     * <code>HubbyEventPlayerInventory</code> event so that it
     * can track inventory changes involving this particular item
     * @param event - the event to handle
     * @return boolean - was the event handled?
     */
    public boolean handleInventoryEvent(HubbyEvent event) {
        HubbyEventPlayerInventory eventPlayerInventory = (HubbyEventPlayerInventory) event;
        Integer slot = eventPlayerInventory.getEventParam(HubbyEventPlayerInventory.KEY_SLOT, Integer.class);
        ItemStack oldStack = eventPlayerInventory.getEventParam(HubbyEventPlayerInventory.KEY_OLD_ITEM_STACK, ItemStack.class);
        ItemStack newStack = eventPlayerInventory.getEventParam(HubbyEventPlayerInventory.KEY_NEW_ITEM_STACK, ItemStack.class);
        boolean isArmorItem = eventPlayerInventory.getEventParam(HubbyEventPlayerInventory.KEY_IS_ARMOR_INVENTORY, Boolean.class);
        boolean canAddInstance = newStack != null && this.getClass().isInstance(newStack.getItem());
        NBTTagCompound newDetails = canAddInstance ? newStack.getSubCompound(getTagName(), true) : null;
        NBTTagCompound oldDetails = oldStack != null ? oldStack.getSubCompound(getTagName(), false) : null;
        boolean handledInventoryEvent = false;
        boolean addedAnInstance = false;
        
        // Quick sanity check... if we are adding an item (and not removing one first),
        // then we want to check our limit to make sure we can perform the add.
        if (hasReachedInstanceLimit() && canAddInstance && oldStack == null) {
            return true;
        }

        // This is when the user clicks on the instanced item to
        // drag it as indicated by the newStack being null
        if (newStack == null && oldDetails != null) {
            ItemStack oldInstance = getInstanceForName(oldDetails.getString(PROPERTY_NAME));
            INSTANCES.remove(oldInstance);
            handledInventoryEvent = true;
        }
        // We are clicking on an empty slot with a dragged instanced item that we
        // are now dropping into a slot
        else if (oldStack == null && newDetails != null) {
            ItemStack oldInstance = getInstanceForName(newDetails.getString(PROPERTY_NAME));
            INSTANCES.remove(oldInstance);

            if (canAddInstance) {
                if (newDetails.getKeySet().isEmpty()) {
                    this.initializeInstanceData(newDetails, slot);
                }
                else {
                    newDetails.setInteger(PROPERTY_SLOT, slot);
                }
                newStack.setTagInfo(getTagName(), newDetails);
                INSTANCES.add(newStack);
                addedAnInstance = true;
            }
            handledInventoryEvent = true;
        }
        // are we swapping one instanced item for another? This does not only happen
        // when you switch one instanced item for another, but simply opening the player's
        // inventory will cause Minecraft to copy the currently stored ItemStack's for some
        // reason and so we need to update the reference in this case
        else if (oldDetails != null && newDetails != null) {

            // remove the existing instance and update it with the new one
            ItemStack oldInstance = this.getInstanceForName(oldDetails.getString(PROPERTY_NAME));
            INSTANCES.remove(oldInstance);

            // add the new instance if it is a matching item
            if (canAddInstance) {
                if (newDetails.getKeySet().isEmpty()) {
                    this.initializeInstanceData(newDetails, slot);
                }
                else {
                    newDetails.setInteger(PROPERTY_SLOT, slot);
                }
                newStack.setTagInfo(getTagName(), newDetails);
                INSTANCES.add(newStack);
                addedAnInstance = true;
            }
            handledInventoryEvent = true;
        }
        
        // did we do anything to the inventory?
        return handledInventoryEvent;
    }
    
    /**
     * Called when the player leaves the game. Here we want to reset our
     * state so that we don't have any unwanted artifacts hanging around
     * that will mess up the next time the player joins a game (if they do
     * so without quitting the app altogether).
     * @param event - the quit game event
     * @return
     */
    public boolean handlePlayerQuitsGameEvent(HubbyEvent event) {
        INSTANCES.clear();
        return true;
    }

    /**
     * Returns the name for this particular item
     * @return String - the name of the item
     */
    @Override
    public String getName() {
        return "pleaseOverrideMe";
    }

    /**
     * Returns a unique name that can be used for identifying instances of
     * this particular item
     * @return
     */
    public String getInstancedName() {
        return String.format("%s_%d", getName(), ++_instancedNameCounter);
    }

    /**
     * Returns the ItemStack by name
     * @param name - the display name to match
     * @return ItemStack - the item instance (null if it could not be found)
     */
    protected ItemStack getInstanceForName(String name) {
        for (ItemStack stack : INSTANCES) {
            NBTTagCompound compound = stack.getSubCompound(getTagName(), false);
            assert compound != null : "Invalid ItemStack stored in the active glowsticks list";
            if (compound.getString(PROPERTY_NAME).equals(name)) {
                return stack;
            }
        }
        return null;
    }

    /**
     * Should return an array containing all of the valid property names for
     * this particular item type
     * @return
     */
    public abstract String[] getPropertyNames();

    /**
     * Returns whether or not this item must be
     * equipped in order to be considered enabled
     * @return
     */
    public abstract boolean mustBeEquipped();

    /**
     * Returns whether or not this item can have its
     * enabled state toggled, if not, then the item is
     * assumed to always be enabled
     * @return boolean - are we enabled?
     */
    public abstract boolean canBeDisabled();

    /**
     * Returns the name of the tag used to store the
     * <code>NBTTagCompound</code> on the <code>ItemStack</code>
     * @return String - the tag name for the compound data
     */
    public abstract String getTagName();

    /**
     * This method is to be implemented by all inheriting classes as a way to
     * build the collection of data (in its initial state) that describes each
     * instance of this item
     * @param compound - the compound to populate
     * @param slot - the slot that this instance has been placed in
     * @return NBTTagCompound - returns the same compound that is passed in
     */
    public abstract NBTTagCompound initializeInstanceData(NBTTagCompound compound, Integer slot);
}
