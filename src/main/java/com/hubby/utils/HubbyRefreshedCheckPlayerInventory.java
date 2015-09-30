package com.hubby.utils;

import com.hubby.utils.HubbyConstants.LogChannel;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;

public class HubbyRefreshedCheckPlayerInventory extends HubbyRefreshedObjectInterface {

    /**
     * Members
     */
    private EntityPlayerSP _player = null;
    private ItemStack[] _inventory = new ItemStack[HubbyConstants.HOTBAR_INVENTORY_SIZE];
    
    /**
     * Constructor
     * @param id - the id of the object
     * @param priority - the priority level, 0 is most
     */
    public HubbyRefreshedCheckPlayerInventory(String id, int priority) {
        super(id, priority);
    }

    /**
     * On refresh we detect changes in the player's inventory and report those
     * changes using the log channels
     */
    @Override
    public void refresh(Long delta, Long elapsed) {
     
        // check for the player and leave if we cant find them
        _player = _player != null ? _player : HubbyUtils.getClientPlayer();
        if (_player == null) {
            return;
        }

        // iterate over the player's main inventory and compare the inventory
        // there with what we have stored here to see the differences
        for (int i = 0; i < HubbyConstants.HOTBAR_INVENTORY_SIZE; ++i) {
            ItemStack curItemStack = _player.inventory.mainInventory[i];
            if (curItemStack != _inventory[i]) {
                if (_inventory[i] == null && curItemStack != null) {
                    _inventory[i] = curItemStack;
                    LogChannel.INFO.log(HubbyRefreshedCheckPlayerInventory.class, "[PLAYER_INVENTORY_CHANGE][NEW ITEM]      => Slot: %d Name: %s", i, curItemStack.getItem().getUnlocalizedName());
                }
                else if (_inventory[i] != null && curItemStack == null) {
                    LogChannel.INFO.log(HubbyRefreshedCheckPlayerInventory.class, "[PLAYER_INVENTORY_CHANGE][REMOVE ITEM]   => Slot: %d Name: %s", i, _inventory[i].getItem().getUnlocalizedName());
                    _inventory[i] = curItemStack;
                }
                else {
                    LogChannel.INFO.log(HubbyRefreshedCheckPlayerInventory.class, "[PLAYER_INVENTORY_CHANGE][SWAP OLD ITEM] => Slot: %d Name: %s", i, _inventory[i].getItem().getUnlocalizedName());
                    LogChannel.INFO.log(HubbyRefreshedCheckPlayerInventory.class, "[PLAYER_INVENTORY_CHANGE][SWAP NEW ITEM] => Slot: %d Name: %s", i, curItemStack.getItem().getUnlocalizedName());
                    _inventory[i] = curItemStack;
                }
            }
        }
    }
}
