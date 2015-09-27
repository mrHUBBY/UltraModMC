package com.hubby.shared.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.hubby.shared.utils.HubbyConstants.LogChannel;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Class that limits the occurrence of an item type within the player's inventory
 * @author davidleistiko
 */
public class HubbyLimitedInventoryItem extends HubbyRefreshedObjectInterface {

    /**
     * This variable can ben listened for by others who may want
     * to know about the item being placed back into the hand of
     * the player
     */
    private static boolean _inventoryModified;
    
    /**
     * This defines how many of the item that the user can have in
     * different inventory slots
     */
    private Integer _allowedInventoryCount;

    /**
     * The item that is limited
     */
    private Class<? extends Item> _itemClass;

    /**
     * Stores the indices that are pointing to currently valid locations 
     * in the inventory for the limited item
     */
    private List<Integer> _originalIndices = new ArrayList<Integer>();

    /**
     * Constructor
     * @param allowed - the allowed inventory count
     * @param item - the item to limit
     */
    public HubbyLimitedInventoryItem(String id, int allowed, Item item) {
        super(id, HubbyRefreshedObjectInterface.HIGHEST_PRIORITY);
        _allowedInventoryCount = allowed;
        _itemClass = item.getClass();
    }
    
    /**
     * Shifts the allowed amount by a clamp value
     * @param adjust - the amount to adjust
     */
    public void adjustAllowedInventoryCount(int adjust) {
        // this calculation allows for the reslt to be 0 which would mean that
        // the player would not be allowed to have any matching item in his inventory
        _allowedInventoryCount += HubbyMath.clamp(adjust, -_allowedInventoryCount, 9 - _allowedInventoryCount);
    }

    /**
     * Implement the refresh function
     */
    @Override
    public void refresh(Long delta, Long elapsed) {

        EntityPlayer player = HubbyUtils.getClientPlayer();
        if (player != null) {
            // get all matching items and if we have more than we are allowed,
            // then remove the excess items until our limit is satisfied
            Map<Integer, ItemStack> items = HubbyUtils.getInventoryItem(_itemClass);
            int inventoryCount = items.size();
            if (inventoryCount > _allowedInventoryCount) {
                TreeSet<Integer> keys = new TreeSet(items.keySet());
                Iterator it = keys.descendingIterator();

                // log the event
                int diff = inventoryCount - _allowedInventoryCount;
                LogChannel.INFO.log(getClass(), "Removing limited item %s x%d times from the inventory", _itemClass.getName(), diff);

                // remove excess items
                while (it.hasNext() && inventoryCount > _allowedInventoryCount) {
                    
                    HubbyLimitedInventoryItem._inventoryModified = true;

                    // check the index for this item and make sure
                    // it was not one of the original positions
                    Integer inventoryIndex = (Integer) it.next();
                    if (_originalIndices.contains(inventoryIndex)) {
                        continue;
                    }

                    // Replace item in hand with the item that was just placed in the
                    // inventory to let the user know that that item can't be placed
                    ItemStack itemStack = player.inventory.mainInventory[inventoryIndex];
                    ItemStack stackInHand = player.inventory.getItemStack();
                    stackInHand = ItemStack.copyItemStack(itemStack);
                    player.inventory.mainInventory[inventoryIndex] = null;
                    player.inventory.setItemStack(stackInHand);
                    inventoryCount -= 1;
                }
            }
            // otherwise store the indices of the items that are valid
            else {
                _originalIndices.clear();
                for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
                    ItemStack stack = player.inventory.mainInventory[i];
                    if (stack != null && stack.getItem().getClass().isAssignableFrom(_itemClass)) {
                        _originalIndices.add(i);
                    }
                }
            }
        }
    }
    
    /**
     * Quick query to see if the inventory was modified or not. Will always
     * be set whenever any limited item is limited.
     * @return
     */
    public static boolean wasMainInventoryModified() {
        if (HubbyLimitedInventoryItem._inventoryModified) {
            HubbyLimitedInventoryItem._inventoryModified = false;
            return true;
        }
        return false;
    }
}
