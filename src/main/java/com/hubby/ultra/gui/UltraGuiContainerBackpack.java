package com.hubby.ultra.gui;

import com.hubby.ultra.UltraConstants.BackpackType;
import com.hubby.utils.HubbyConstants;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class UltraGuiContainerBackpack extends Container {

    public static final int NUM_ROWS = 3;
    public static final int ITEMS_PER_ROW = 9;
    public static final int STEP_AMOUNT = 18;
    public static final int START_OFFSET_SMALL = 8;
    public static final int START_OFFSET_OTHER = 40;
    public static final int INDEX_OFFSET_SMALL = 0;
    public static final int INDEX_OFFSET_MEDIUM = 4;
    public static final int INDEX_OFFSET_LARGE = 8;
    public static final int INDEX_OFFSET_LARGE_PLUS = 12;
    public static final int BORDER_SIZE = 4;
    public static final int SLOT_INDEX_OFFSET = HubbyConstants.HOTBAR_INVENTORY_SIZE;
    
    private InventoryPlayer _inventoryPlayer = null;
    private UltraGuiInventoryBackpack _inventoryBackpack = null;
    private ScaledResolution _resolution = null;
    private int _slotStartPosX = 0;
    private int _slotStartPosY = 0;
    private int _activeSlotStartPosY = 0;
    private BackpackType _backpackType;
    

    /**
     * Constructor
     * @param playerInventory
     * @param backpackInventory
     */
    public UltraGuiContainerBackpack(InventoryPlayer playerInventory, UltraGuiInventoryBackpack backpackInventory) {
        _inventoryPlayer = playerInventory;
        _inventoryBackpack = backpackInventory;
        _backpackType = backpackInventory.getBackpackType();
    }
    
    /**
     * Returns access to the backpack inventory
     * @return UltraGuiInventoryBackpack - the backpack inventory
     */
    public UltraGuiInventoryBackpack getBackpackInventory() {
        return _inventoryBackpack;
    }

    /**
     * Initialize the gui container
     * @param res - the resolution
     */
    public void initContainer(ScaledResolution resolution) {

        _resolution = resolution;

        // reset these each time
        inventorySlots.clear();
        inventoryItemStacks.clear();

        // Determine the slot starting position
        _slotStartPosX = (int) (START_OFFSET_SMALL * _resolution.getScaleFactor());
        _slotStartPosY = (int) (START_OFFSET_SMALL * _resolution.getScaleFactor());
        _activeSlotStartPosY = (_slotStartPosY + STEP_AMOUNT * NUM_ROWS + 4) * _resolution.getScaleFactor();

        switch (_backpackType) {
        case MEDIUM:
        case LARGE:
            _slotStartPosX += (START_OFFSET_OTHER * _resolution.getScaleFactor());
        case SMALL:
        default:
            break;
        }
        
        // NOTE: First we want to bind the player's inventory to the slots
        // for the hotbar on this backpack container
        
        bindPlayerInventory(_inventoryPlayer);
        
        // Construct all slots with their correct positions and the
        // inventory that they are bound to
        for (int j = 0; j < NUM_ROWS; j++) {
            for (int i = 0; i < ITEMS_PER_ROW; i++) {
                addSlotToContainer(new Slot(_inventoryBackpack, SLOT_INDEX_OFFSET + i + j * ITEMS_PER_ROW, _slotStartPosX + i * STEP_AMOUNT * _resolution.getScaleFactor(), _slotStartPosY + j * STEP_AMOUNT * _resolution.getScaleFactor()));
            }
        }
        
        // Setup the slots and their positions for the medium backpack
        if (_backpackType == BackpackType.MEDIUM) {
            int extraSlotStartX = START_OFFSET_SMALL;
            int extraSlotStartY = START_OFFSET_SMALL;

            // top-left extra slots
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 2; ++j) {
                    int finalSlotX = (int) ((float) (extraSlotStartX + i * STEP_AMOUNT) * (float) _resolution.getScaleFactor());
                    int finalSlotY = (int) ((float) (extraSlotStartY + j * STEP_AMOUNT) * (float) _resolution.getScaleFactor());
                    addSlotToContainer(new Slot(_inventoryBackpack, SLOT_INDEX_OFFSET + NUM_ROWS * ITEMS_PER_ROW + (j + (i * 2)) + INDEX_OFFSET_SMALL, finalSlotX, finalSlotY));
                }
            }

            extraSlotStartX += START_OFFSET_OTHER + ITEMS_PER_ROW * STEP_AMOUNT + BORDER_SIZE;

            // top right extra slots
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 2; ++j) {
                    int finalSlotX = (int) ((float) (extraSlotStartX + i * STEP_AMOUNT) * (float) _resolution.getScaleFactor());
                    int finalSlotY = (int) ((float) (extraSlotStartY + j * STEP_AMOUNT) * (float) _resolution.getScaleFactor());
                    addSlotToContainer(new Slot(_inventoryBackpack, SLOT_INDEX_OFFSET + NUM_ROWS * ITEMS_PER_ROW + (j + (i * 2)) + INDEX_OFFSET_MEDIUM, finalSlotX, finalSlotY));
                }
            }
        }
        // Setup the slots and their positions for the large backpack
        else if (_backpackType == BackpackType.LARGE) {
            int extraSlotStartX = START_OFFSET_SMALL;
            int extraSlotStartY = START_OFFSET_SMALL;

            // top left extra slots
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 2; ++j) {
                    int finalSlotX = (int) ((float) (extraSlotStartX + i * STEP_AMOUNT) * (float) _resolution.getScaleFactor());
                    int finalSlotY = (int) ((float) (extraSlotStartY + j * STEP_AMOUNT) * (float) _resolution.getScaleFactor());
                    addSlotToContainer(new Slot(_inventoryBackpack, SLOT_INDEX_OFFSET + NUM_ROWS * ITEMS_PER_ROW + (j + (i * 2)) + INDEX_OFFSET_SMALL, finalSlotX, finalSlotY));
                }
            }

            // bottom left extra slots
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 2; ++j) {
                    int finalSlotX = (int) ((float) (extraSlotStartX + i * STEP_AMOUNT) * (float) _resolution.getScaleFactor());
                    int finalSlotY = (int) ((float) (extraSlotStartY + j * STEP_AMOUNT + START_OFFSET_OTHER) * (float) _resolution.getScaleFactor());
                    addSlotToContainer(new Slot(_inventoryBackpack, SLOT_INDEX_OFFSET + NUM_ROWS * ITEMS_PER_ROW + (j + (i * 2)) + INDEX_OFFSET_MEDIUM, finalSlotX, finalSlotY));
                }
            }

            extraSlotStartX += START_OFFSET_OTHER + ITEMS_PER_ROW * STEP_AMOUNT + BORDER_SIZE;

            // top right extra slots
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 2; ++j) {
                    int finalSlotX = (int) ((float) (extraSlotStartX + i * STEP_AMOUNT) * (float) _resolution.getScaleFactor());
                    int finalSlotY = (int) ((float) (extraSlotStartY + j * STEP_AMOUNT) * (float) _resolution.getScaleFactor());
                    addSlotToContainer(new Slot(_inventoryBackpack, SLOT_INDEX_OFFSET + NUM_ROWS * ITEMS_PER_ROW + (j + (i * 2)) + INDEX_OFFSET_LARGE, finalSlotX, finalSlotY));
                }
            }

            // bottom right extra slots
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 2; ++j) {
                    int finalSlotX = (int) ((float) (extraSlotStartX + i * STEP_AMOUNT) * (float) _resolution.getScaleFactor());
                    int finalSlotY = (int) ((float) (extraSlotStartY + j * STEP_AMOUNT + 40) * (float) _resolution.getScaleFactor());
                    addSlotToContainer(new Slot(_inventoryBackpack, SLOT_INDEX_OFFSET + NUM_ROWS * ITEMS_PER_ROW + (j + (i * 2)) + INDEX_OFFSET_LARGE_PLUS, finalSlotX, finalSlotY));
                }
            }
            
            // adds the player inventory to our inventory screen so that the player can
            // transfer items from their main inventory and in and out of their backpack
            //bindPlayerInventory(_inventoryPlayer);
            
            // validate that the inventory is correct size
            switch (_backpackType) {
            case SMALL:
                assert inventorySlots.size() == 27 + HubbyConstants.HOTBAR_INVENTORY_SIZE : "[UltraGuiContainerBackpack] Failed to create correct number of slots for backpack container";
            case MEDIUM:
                assert inventorySlots.size() == 35 + HubbyConstants.HOTBAR_INVENTORY_SIZE : "[UltraGuiContainerBackpack] Failed to create correct number of slots for backpack container";
            case LARGE:
                assert inventorySlots.size() == 43 + HubbyConstants.HOTBAR_INVENTORY_SIZE : "[UltraGuiContainerBackpack] Failed to create correct number of slots for backpack container";
            default:
                break;
            }
        }
    }

    /**
     * Adds the player's main inventory to this container so that the player
     * can swap items between their main inventory and the backpack inventory
     * @param inventoryPlayer - the player's inventory to bind
     */
    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < ITEMS_PER_ROW; ++i) {
            addSlotToContainer(new Slot(inventoryPlayer, i, (_slotStartPosX + i * STEP_AMOUNT) * _resolution.getScaleFactor(), _activeSlotStartPosY));
        }
    }

    /**
     * Can the player interact with this container
     * @param player - the entity player
     * @return boolean - can we use this inventory
     */
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
    
    /**
     * Called when the corresponding gui gets closed
     * @param player - the player
     */
    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
    }
    
    /**
     * Puts the stack into the slot at the index specified as long
     * as the inventories match
     */
    @Override
    public void putStackInSlot(int slotIndex, ItemStack stack) {
        Slot s = getSlot(slotIndex); 
        if (s.inventory == _inventoryBackpack) {
            super.putStackInSlot(slotIndex, stack);
        }
        // if we get here, it should be that the inventory is of type
        // InventoryPlayer, which means we are storing the hotbar item stacks
        else {
            super.putStackInSlot(slotIndex, stack);
        }
    }
}
