package com.hubby.ultra.gui;

import com.hubby.shared.utils.HubbyConstants;
import com.hubby.ultra.UltraConstants.BackpackType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IChatComponent;

/**
 * This class manages the inventory for the backpack item
 * @author davidleistiko
 */
public class UltraGuiInventoryBackpack implements IInventory {
    /**
     * Members
     */
    private BackpackType _backpackType;
    private int _maxInventoryStackSize;
    private int _inventorySize;
    private ItemStack[] _inventoryItems;
    private boolean _needsRefresh;

    /**
     * Constructor
     * @param backpackType - the backpack type for this inventory
     */
    public UltraGuiInventoryBackpack(BackpackType backpackType) {
        _backpackType = backpackType;
        _maxInventoryStackSize = _backpackType.getInventoryStackSizeLimit();
        _inventorySize = _backpackType.getInventorySize() + HubbyConstants.HOTBAR_INVENTORY_SIZE;
        _inventoryItems = new ItemStack[_inventorySize];
    }
    
    /**
     * Returns access to the backpack type
     * @return BackpackType - the type of backpack
     */
    public BackpackType getBackpackType() {
        return _backpackType;
    }

    /**
     * Change the size of the inventory
     * @param newSize - the new size for the inventory
     */
    public void resize(int newSize) {
        ItemStack[] newArray = new ItemStack[newSize];
        System.arraycopy(_inventoryItems, 0, newArray, 0, Math.min(_inventoryItems.length, newSize));
        _inventoryItems = newArray;
        _inventorySize = newSize;
    }

    /**
     * Returns the size of the inventory
     * @return int - the inventory size
     */
    @Override
    public int getSizeInventory() {
        return _inventorySize;
    }

    /**
     * Returns the item found in the slot specified
     * @param int - the slot to lookup
     * @return ItemStack - the item at the slot position
     */
    @Override
    public ItemStack getStackInSlot(int slot) {
        return _inventoryItems[slot];
    }

    /**
     * Mark the inventory as being dirty
     */
    @Override
    public void markDirty() {
        _needsRefresh = true;
    }

    /**
     * Called when the inventory is opened
     */
    @Override
    public void openInventory(EntityPlayer player) {
    }

    /**
     * Called when the invetory is to be closed
     */
    @Override
    public void closeInventory(EntityPlayer player) {
        if (_needsRefresh) {
        }
    }
    
    /**
     * Saves the inventory to disk for later?
     * @param list - the list to save to
     */
    public void save(NBTTagList list) {
        // NOTE:
        // It is assumed that for this inventory, it is storing the player's main
        // inventory in the first 9 spaces, so we want to skip those since we only
        // care about what is in the backpack
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("slot", (byte)i);
                stack.writeToNBT(tag);
                list.appendTag(tag);
            }
        }
    }
   
    /**
     * Loads the inventory from disk
     * @param list - the list to read from
     */
    public void load(NBTTagList list) {
        
        // wipe out all current contents before we start
        // loading the saved data
        clear();
        
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = (NBTTagCompound)list.getCompoundTagAt(i);
            byte slot = tag.getByte("slot");
            ItemStack is = ItemStack.loadItemStackFromNBT(tag);
            if (slot >= 0 && slot < getSizeInventory()) {
                setInventorySlotContents(slot, ItemStack.copyItemStack(is));
            }
        }
    }

    /**
     * Returns if the identified item can be placed in the slot specified
     * @param slot - the slot to place the item in
     * @param itemStack - the item in question
     * @return boolean - can the item be placed in the slot?
     */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        return true;
    }

    /**
     * Decrement the item stack size by count found at the slot specified
     * @param slot - the slot for the item
     * @param count - the count to adjust the size by
     * @return ItemStack - the new item stack
     */
    @Override
    public ItemStack decrStackSize(int slot, int count) {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null) {
            if (stack.stackSize <= count) {
                setInventorySlotContents(slot, null);
            }
            else {
                stack = stack.splitStack(count);
                if (stack.stackSize == 0) {
                    setInventorySlotContents(slot, null);
                }
            }
        }
        return stack;
    }

    /**
     * Returns the item stack in the specified slot
     * @param slot - the slot to get the item
     * @return ItemStack - the item stack at the slot specified
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null) {
            setInventorySlotContents(slot, null);
        }
        return stack;
    }

    /**
     * Sets the <code>ItemStack</code> for the slot specified
     * @param slot - the slot for the item
     * @param itemStack - the stack to set in the slot
     */
    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        _inventoryItems[slot] = itemStack;
        markDirty();
    }

    /**
     * Returns the name for this inventory
     * @return
     */
    @Override
    public String getName() {
        return _backpackType.getContainerName();
    }

    /**
     * Returns if we have a custom name
     */
    @Override
    public boolean hasCustomName() {
        return true;
    }

    /**
     * Returns the max stack size for this inventory
     * @return int - the max allowed stack size
     */
    @Override
    public int getInventoryStackLimit() {
        return _maxInventoryStackSize;
    }

    /**
     * Is this inventory useable by the player?
     * @param player - the player
     * @return boolean - is the inventory useable?
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    /**
     * Gets the display name for chat
     * @return IChatComponent - returns null
     */
    @Override
    public IChatComponent getDisplayName() {
        return null;
    }

    /**
     * Helper method for returning custom field by id
     * @param id - the id of the field to lookup
     * @return int - the value of the field (0 by default)
     */
    @Override
    public int getField(int id) {
        return 0;
    }

    /**
     * Helper method for storing custom int field by id
     * @param id - the field id
     * @param value - the field value
     */
    @Override
    public void setField(int id, int value) {
    }

    /**
     * Returns the number of fields
     * @return int - the field count
     */
    @Override
    public int getFieldCount() {
        return 0;
    }

    /**
     * Clears the inventory contents
     */
    @Override
    public void clear() {
        for (int i = 0; i < getSizeInventory(); ++i) {
            this.setInventorySlotContents(i, null);
        }
    }
}
