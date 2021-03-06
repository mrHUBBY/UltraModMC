package com.hubby.ultra.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.hubby.gui.HubbyGuiButton;
import com.hubby.network.HubbyClientPacketSenderHelper;
import com.hubby.network.HubbyNetworkHelper;
import com.hubby.ultra.UltraConstants.BackpackType;
import com.hubby.ultra.items.UltraItemBackpack;
import com.hubby.ultra.setup.UltraMod;
import com.hubby.utils.HubbyColor;
import com.hubby.utils.HubbyColor.ColorMode;
import com.hubby.utils.HubbyConstants;
import com.hubby.utils.HubbyConstants.ClickButton;
import com.hubby.utils.HubbyConstants.ClickType;
import com.hubby.utils.HubbyConstants.LogChannel;
import com.hubby.utils.HubbyInputFilter;
import com.hubby.utils.HubbySavePersistentDataHelper;
import com.hubby.utils.HubbySlicedResource;
import com.hubby.utils.HubbyUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * The Guiscreen for the backpack item
 * @author davidleistiko
 */
public class UltraGuiScreenBackpackPlus extends InventoryEffectRenderer  {
    
    /**
     * Members
     */
    private static final int NAME_INPUT_ID = 0;
    private static final int LOAD_BUTTON_ID = 1;
    private static final int SAVE_BUTTON_ID = 2;
    private static final int SIZE_X = 256;
    private static final int SIZE_Y = 110;
    private static final HubbyInputFilter KEY_FILTER = new HubbyInputFilter("eE");
    private static final String INPUT_PLACEHOLDER = "Enter backpack name...";
    private static final NBTTagList INVENTORY_TAGLIST = new NBTTagList();
    private static final ResourceLocation[] GUI_BACKGROUND_RESOURCES = new ResourceLocation[] { new ResourceLocation(HubbyUtils.getResourceLocation(UltraMod.MOD_ID, "textures/gui/gui_ultra_backpack_small_container.png")),
                                                                                                new ResourceLocation(HubbyUtils.getResourceLocation(UltraMod.MOD_ID, "textures/gui/gui_ultra_backpack_medium_container.png")),
                                                                                                new ResourceLocation(HubbyUtils.getResourceLocation(UltraMod.MOD_ID, "textures/gui/gui_ultra_backpack_large_plus_container.png"))};
    
    private UltraGuiContainerBackpack _backpackContainer = null;
    private UltraGuiInventoryBackpack _backpackInventory = null;
    private ScaledResolution _resolution = null;
    private int _inventorySize = 0;
    private int[] _sizeWidths = new int[] { xSize, 256, 256 };
    private BackpackType _backpackType;
    private UltraItemBackpack _backpackItem;
    private EntityPlayer _thePlayer;
    private GuiTextField _inputField = null;
    private GuiButton _loadButton = null;
    private GuiButton _saveButton = null;
        

    /**
     * Constructor
     * @param [EntityPlayer par1EntityPlayer] The active player
     */
    public UltraGuiScreenBackpackPlus(EntityPlayer player) {
        super(new UltraGuiContainerBackpack(player.inventory, new UltraGuiInventoryBackpack(UltraGuiScreenBackpack.getBackpackType(player))));
        player.openContainer = inventorySlots;
        allowUserInput = true;
        
        // Get the backpack item and type
        ItemStack stack = player.getCurrentEquippedItem();
        assert UltraItemBackpack.class.isInstance(stack.getItem()) : "[UltraGuiScreenBackpack] Opened backpack gui when a backpack is not the currently equipped item on the player!";
        
        // NOTE: We don't want to cache the player that was passed in here
        // as it is not the version of the player we want... we need the client
        // version of the player... (should be this way for all guis)
        _thePlayer = player;// HubbyUtils.getClientPlayer();
        _backpackItem = (UltraItemBackpack)stack.getItem();
        _backpackType = _backpackItem.getBackpackType();
        _inventorySize = _backpackType.getInventorySize();
        _backpackContainer = (UltraGuiContainerBackpack)inventorySlots;
        _backpackInventory = _backpackContainer.getBackpackInventory();
    }
    
    /**
     * Helper method to retrieve the backpack type
     * @param player - the player
     * @return BackpackType - the type of backpack the player has equipped
     */
    public static BackpackType getBackpackType(EntityPlayer player) {
        ItemStack stack = player.getCurrentEquippedItem();
        assert UltraItemBackpack.class.isInstance(stack.getItem()) : "[UltraGuiScreenBackpack] Opened backpack gui when a backpack is not the currently equipped item on the player!";
        UltraItemBackpack item = (UltraItemBackpack)stack.getItem();
        return item.getBackpackType();
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        Keyboard.enableRepeatEvents(true);

        // initialize the container
        _resolution = new ScaledResolution(mc, xSize, ySize);
        _backpackContainer.initContainer(_resolution);
      
        // TODO: May need to use > instead of >=
        // adjust the xSize based on backpack type
//        if (_backpackType.getValue() > BackpackType.SMALL.getValue()) {
//            xSize = _sizeWidths[_backpackType.getValue()];
//        }
        xSize = _sizeWidths[_backpackType.getValue()];

        // update these variables now, if we don't, the container
        // will be confused with the size
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        
        int x1 = (width - xSize) / 2;
        int y1 = (height - ySize) / 2;
        int inputX = (int) ((float) (x1 + 69));
        int inputY = (int) ((float) (y1 + 91));

        _inputField = new GuiTextField(NAME_INPUT_ID, this.fontRendererObj, inputX, inputY, 113, 14);
        _inputField.setMaxStringLength(64);
        _inputField.setEnableBackgroundDrawing(false);
        _inputField.setFocused(false);
        _inputField.setText(INPUT_PLACEHOLDER);
        _inputField.setTextColor(HubbyColor.convertToMinecraftColor(0xFFFFFFFF));
        _inputField.setCanLoseFocus(true);
        _inputField.setFocused(false);
        _inputField.setCursorPosition(0);
        
        this.buttonList.clear();
        
        // Build the constraints for the stretch button
        String resource = HubbyUtils.getResourceLocation(UltraMod.MOD_ID, "textures/gui/gui_ultra_button.png");
        Map<String, Integer> constraints = new HashMap<String, Integer>();
        constraints.put(HubbySlicedResource.CONSTRAINT_CORNER_SIZE, 3);
        constraints.put(HubbySlicedResource.CONSTRAINT_RESOURCE_STEP, 8);
        constraints.put(HubbySlicedResource.CONSTRAINT_MIN_WIDTH, 8);
        constraints.put(HubbySlicedResource.CONSTRAINT_MIN_HEIGHT, 8);
        constraints.put(HubbySlicedResource.CONSTRAINT_TEXTURE_WIDTH, 32);
        constraints.put(HubbySlicedResource.CONSTRAINT_TEXTURE_HEIGHT, 64);
        
        int buttonX = (width - SIZE_X) / 2 + 7;
        int buttonY = (height - SIZE_Y) / 2 + SIZE_Y - 51;
        _loadButton = new HubbyGuiButton(resource, LOAD_BUTTON_ID, buttonX, buttonY, 53, 16, "Load", constraints);
        this.buttonList.add(_loadButton);
        
        buttonX = (width - SIZE_X) / 2 + SIZE_X - 60;
        buttonY = (height - SIZE_Y) / 2 + SIZE_Y - 51;
        _saveButton = new HubbyGuiButton(resource, SAVE_BUTTON_ID, buttonX, buttonY, 53, 16, "Save", constraints);
        this.buttonList.add(_saveButton);
        
        // clear all of the slots with itemstack
        clearInventory();

        // Loads the saved backpack inventory
        loadBackpackInventory();
        
        // loads the player inventory
        loadPlayerInventory();

        // TODO: Hide potion status icons (implement)
        // NOTE: this must be done after the super call to initGui
        //NitroInterface.hidePotionStatusIcons(this, getClass().getSuperclass());
    }

    /**
     * Handle the gui closing and save the inventory changes to disk
     */
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        saveBackpackInventory();
    }

    /**
     * Update called every tick
     */
    @Override
    public void updateScreen() {
        super.updateScreen();
        
        if (_inputField != null) {
            _inputField.updateCursorCounter();
        }
    }
    
    /**
     * Respond to mouse clicked event
     * @param mouseX - the x position of the mouse
     * @param mouseY - the y position of the mouse
     * @param mouseButton - the button of the mouse that was clicked
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        _inputField.mouseClicked(mouseX, mouseY, mouseButton);

        // if the input field has focus then remove the placeholder text
        updateInputFields();

        // call default behavior
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    /**
     * Updates the input fields so that their state matches what the user
     * is doing in terms of input
     */
    protected void updateInputFields() {
        
        // if the input field has focus then remove the placeholder text
        if (_inputField.isFocused()) {
            if (_inputField.getText().equals(INPUT_PLACEHOLDER)) {
                _inputField.setText("");
                _inputField.setTextColor(HubbyColor.convertToMinecraftColor(0xFF0000FF));
            }
        }
        // otherwise, set the placeholder text if the current value is the empty string
        else {
            if (_inputField.getText().equals("")) {
                _inputField.setText("Enter name...");
                _inputField.setTextColor(HubbyColor.convertToMinecraftColor(0xFFFFFFFF));
            }
        }
    }
    
    /**
     * Callback for when a key is typed
     * @param keyChar - the typed character
     * @param keyCode - the corresponding code for the character
     */
    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
        boolean addedInput = _inputField.textboxKeyTyped(keyChar, keyCode);

        // Don't pass the typed key to the parent if the character
        // in question is within the filtered character list as we
        // don't want the base behavior to interfere with what we
        // are trying to do
        if (!KEY_FILTER.isFiltered(keyChar)) {
            super.keyTyped(keyChar, keyCode);
        }
        
        // Handle cycling between inputs
        if (keyCode == Keyboard.KEY_TAB) {
            if (_inputField.isFocused()) {
                _inputField.setFocused(false);
            }
            else {
                _inputField.setFocused(true);
            }
            updateInputFields();   
            return;
        }
        
        // updates the color of the input field to let the user
        // know that they have entered a name that is already being used
        boolean isValid = isInputValid(_inputField.getText());
        //GuiButton btn = (GuiButton)this.buttonList.get(0);
        //btn.enabled = isValid && !_inputField.getText().equals(INPUT_PLACEHOLDER);
        
        // Do we have valid input? If so set the appropriate color, if disabled
        // then determine if we have bad input or no input at all
        if (addedInput && isValid) {
            _inputField.setTextColor(HubbyColor.convertToMinecraftColor(0x00FF00FF));
        }
        else if (addedInput && !isValid) {           
           if (_inputField.getText().length() > 0) {
                _inputField.setTextColor(HubbyColor.convertToMinecraftColor(0xFF0000FF));
            }
            else {
                _inputField.setTextColor(HubbyColor.convertToMinecraftColor(0xFFFFFFFF));
            }
        }
    }
    
    /**
     * Returns whether or not the input is considered valid by checking
     * to see if we currently have any waypoints listed by the name that
     * is in the input field
     * @return boolean - is the input valid?
     */
    protected boolean isInputValid(String text) {
        return text.length() > 0;
    }
    
    /**
     * Draws the foreground container layer
     * @param mouseX - the mouse position x
     * @param mouseY - the mouse position y
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String text = StatCollector.translateToLocal(_backpackType.getContainerName());
        int f = _resolution.getScaleFactor();
        int w = xSize;
        int h = _resolution.getScaledHeight();
        int strW = fontRendererObj.getStringWidth(text);
        int xPos = f * (w - strW) / 2;
        int yPos = -18 * f;
        drawString(fontRendererObj, text, xPos, yPos, (int)HubbyColor.WHITE.getPackedColor(ColorMode.MINECRAFT));
    }

    /**
     * Draw the gui container background layer
     * @param partialTicks - the number of elapsed partial ticks
     * @param mouseX - the x position for the mouse
     * @param mouseY - the y position for the mouse
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

        // TODO:
        // Should we use itemDamage instead of backpackType ??
        mc.renderEngine.bindTexture(GUI_BACKGROUND_RESOURCES[_backpackType.getValue()]);
        
        // Enable the correct transparent texture to be
        // draw for the input textfield background
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.8f);
        
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    /**
     * Draws the entire screen
     * @param mouseX - the x position for the mouse
     * @param mouseY - the y position for the mouse
     * @param partialTicks - the number of elapsed partial ticks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        // This is needed so that the input-field draws
        // the text correctly (ie not faded, but nice and bright)
        RenderHelper.enableGUIStandardItemLighting();
        if (_inputField != null) {
            _inputField.drawTextBox();
        }
    }
    
    /**
     * Clears the inventory
     */
    public void clearInventory() {
        _backpackInventory.clear();
    }
    
    /**
     * Loads the player inventory
     */
    public void loadPlayerInventory() {
        for (int i = 0; i < HubbyConstants.HOTBAR_INVENTORY_SIZE; ++i) {
            ItemStack stack = _thePlayer.inventory.mainInventory[i];
            _backpackContainer.putStackInSlot(i, ItemStack.copyItemStack(stack));
        }
    }

    /**
     * Based on the currently equipped backpack, this function inits the backpack inventory
     * with the items that were last saved to disk if there are any, otherwise, the inventory
     * will be empty to start, but the player can transfer any of their main inventory items
     * into the backpack whenever they want
     */
    public boolean loadBackpackInventory() {
        String inventoryFilename = _backpackType.getInventoryFilename();
        
        // lets start with a fresh inventory every time
        _backpackInventory.clear();

        // Get the saved tagCompound for loading
        NBTTagCompound tagCompound = HubbySavePersistentDataHelper.getInstance().loadTagCompound(inventoryFilename);
        if (tagCompound != null) {
            NBTTagList list = tagCompound.getTagList("inventory", 10);
            _backpackInventory.load(list);
            return true;
        }
        
        // no tag compound to load, starting with an empty backpack inventory
        return false;
    }
    
    /**
     * Saves the current contents of the backpack inventory to disk for
     * later retrieval
     */
    public void saveBackpackInventory() {
        String inventoryFilename = _backpackType.getInventoryFilename();
        NBTTagCompound tagCompound = new NBTTagCompound();
        NBTTagList tagList = new NBTTagList();
        _backpackInventory.save(tagList);
        tagCompound.setTag("inventory", tagList);
        HubbySavePersistentDataHelper.getInstance().saveTagCompound(inventoryFilename, tagCompound);
    }

    /**
     * Handle when the user clicks the mouse on this gui screen
     * @param slotIn - the slot that was clicked on
     * @param slotId - the id for the slot
     * @param clickedButton - the mouse button that the user clicked
     * @param clickType - was it a double click or single ?
     */
    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {

        boolean userShiftClicked = clickType == ClickType.SHIFT_CLICK.getValue();
        clickType = slotId == -999 && clickType == 0 ? 4 : clickType;
        ClickType click = ClickType.getEnumForValue(clickType);
        ClickButton button = ClickButton.getEnumForValue(clickedButton);

        ItemStack itemStack = null;
        InventoryPlayer inventoryPlayer = _thePlayer.inventory;
        Integer inventorySize = _backpackType.getInventorySize();
        Integer slotNumber = slotIn != null ? slotIn.slotNumber : slotId;

        // This signifies when the user drops an item of the edge of the gui container
        // thereby dropping the item into the world
        if (slotIn == null && click != ClickType.UNKNOWN) {

            // does the user have an active item that they are in the process of dropping?
            if (inventoryPlayer.getItemStack() != null) {
                
                // did the user perform a left-click... if so, then we drop the item in the world
                // and set the item in the player's hand to be empty
                if (button == ClickButton.LEFT_BUTTON) {
                    ItemStack toDrop = inventoryPlayer.getItemStack();
                    _thePlayer.dropPlayerItemWithRandomChoice(toDrop, true);
                    mc.playerController.sendPacketDropItem(toDrop);
                    inventoryPlayer.setItemStack(null);
                    
                    LogChannel.INFO.log(UltraGuiScreenBackpack.class, "Dropping! Dropping item {%s} x%d from player's hand into the world", toDrop.getItem().getUnlocalizedName(), toDrop.stackSize);
                }
                // did the user perform a right-click... if so then we split the stack by dropping
                // 1 occurrence of the item in the player's hand leaving the player with the same
                // item in hand -1 count, unless the player only had 1 for the stack size then the
                // player will be left with nothing in hand
                else if (button == ClickButton.RIGHT_BUTTON) {
                    ItemStack toDrop = inventoryPlayer.getItemStack().splitStack(1);
                    _thePlayer.dropPlayerItemWithRandomChoice(toDrop, true);
                    mc.playerController.sendPacketDropItem(toDrop);

                    // if the player is left with none for the stack size then
                    // clear the item in the player's hand
                    if (inventoryPlayer.getItemStack().stackSize == 0) {
                        inventoryPlayer.setItemStack(null);
                    }
                    
                    LogChannel.INFO.log(UltraGuiScreenBackpack.class, "Dropping! Dropping item {%s} x1 from player's hand into the world", toDrop.getItem().getUnlocalizedName());
                }
            }
            
            // we can return from here now
            return;
        }
        
        // if we get here, then we know that the player is interacting with the 
        // container and inventory by clicking on one of the valid slots to
        // either drop an item or pick an item up

        // This test is to see if we clicked on the backpack inventory
        if (click != ClickType.UNKNOWN && slotIn.inventory == _backpackInventory) {
            
            itemStack = inventoryPlayer.getItemStack();
            ItemStack slotItemStack = slotIn.getStack();
            ItemStack otherItemStack = null;

            // Did the user click on the hotbar (the player's main inventory)
            if (click == ClickType.HOTBAR) {

                // did we click on the player's main inventory with a valid active slot?
                // if we did then copy the active item from the slot into the player's
                // main inventory
                ClickButton adjustedClick = ClickButton.getEnumForValue(clickedButton + ClickButton.getHotbarValueOffset());
                if (slotItemStack != null && adjustedClick.isHotbar()) {
                    otherItemStack = ItemStack.copyItemStack(slotItemStack);
                    _thePlayer.inventoryContainer.putStackInSlot(adjustedClick.getValue(), otherItemStack);
                    _thePlayer.inventoryContainer.detectAndSendChanges();
                    
                    LogChannel.INFO.log(UltraGuiScreenBackpack.class, "Hotbar! Dropping item %s onto the player's hotbar at position %d!", otherItemStack.getItem().getUnlocalizedName(), adjustedClick.getValue());
                }
                                
                // nothing more to do
                return;
            }

            // Did the user pick a block
            if (click == ClickType.PICK_BLOCK) {

                // Does the user have nothing in hand and does the selected slot have an item?
                // If that is so, then we are setting the item that should be in the player's hand
                if (inventoryPlayer.getItemStack() == null && slotIn.getHasStack()) {
                    otherItemStack = ItemStack.copyItemStack(slotIn.getStack());
                    inventoryPlayer.setItemStack(otherItemStack);
                    
                    LogChannel.INFO.log(UltraGuiScreenBackpack.class, "PickBlock! Setting player's item in hand with item %s", otherItemStack.getItem().getUnlocalizedName());
                }
                // nothing more to do here
                return;
            }

            // Is the player dropping the active slot
            if (click == ClickType.DROP && slotItemStack != null) {
                
                // Determine stack size, if the user performed a left-click then we are dropping
                // only 1 of the items from the active slot, otherwise, for right-click, we drop
                // the entire stack of the item
                int stackSize = button == ClickButton.LEFT_BUTTON ? 1 : slotItemStack.getMaxStackSize();
                otherItemStack = ItemStack.copyItemStack(slotItemStack);
                otherItemStack.stackSize = stackSize;
                
                _thePlayer.dropPlayerItemWithRandomChoice(otherItemStack, true);
                mc.playerController.sendPacketDropItem(otherItemStack);
                
                LogChannel.INFO.log(UltraGuiScreenBackpack.class, "Dropping! Dropping item {%s} x1 from player's hand into the world", otherItemStack.getItem().getUnlocalizedName());
                return;
            }

            // Here we are testing to see if the player more or less clicked on a slot that corresponds to the same item
            // that is currently in the player's hand
            if (itemStack != null && slotItemStack != null && itemStack.isItemEqual(slotItemStack) && ItemStack.areItemStackTagsEqual(itemStack, slotItemStack)) {

                // If the user clicked the left button, then they are basically clicking on the same item
                // in the inventory that is also in their hand
                if (button == ClickButton.LEFT_BUTTON) {

                    // if the user performed a shift click then add the max amount
                    // of the item to the stack that is in the player's hand
                    if (userShiftClicked) {
                        itemStack.stackSize = itemStack.getMaxStackSize();
                    }
                    // otherwise we just add one more to the item stack that is
                    // in the player's hand
                    else if (itemStack.stackSize < itemStack.getMaxStackSize()) {
                        ++itemStack.stackSize;
                    }
                }
                // the user performed a right-click, so here we are taking what
                // is in the player's hand and placing 1 back into the inventory
                // at the active slot
                else if (itemStack.stackSize <= 1) {
                    inventoryPlayer.setItemStack(null);
                }
                else {
                    --itemStack.stackSize;
                }
                
                LogChannel.INFO.log(UltraGuiScreenBackpack.class, "Adjusting! Changing stack size for the item %s that is in the players' hand", itemStack.getItem().getUnlocalizedName());
            }
            // Their is a valid item in the active slot and the player currently
            // has nothing in their hands... here we place the active slot stack
            // into the player's hand
            else if (slotItemStack != null && itemStack == null) {
                inventoryPlayer.setItemStack(ItemStack.copyItemStack(slotItemStack));
                itemStack = inventoryPlayer.getItemStack();

                // TODO:
                // Do we need to do more to simulate this on the server/client
                // Because the player took the active slot stack we need to simulate that by clearing
                // the stack in the corresponding slot in the active backpack container
                _backpackContainer.putStackInSlot(slotIn.slotNumber, null);

                // if the user performed a shift-click then the user will be picking up
                // a full stack of the item contained therein rather than the default
                // pickup stack size of 1
                if (userShiftClicked) {
                    itemStack.stackSize = itemStack.getMaxStackSize();
                }
                
                LogChannel.INFO.log(UltraGuiScreenBackpack.class, "Moving! Moved the item %s from the inventory and into the player's hand", itemStack.getItem().getUnlocalizedName());
            }
            // If we get here then that means the player is swapping the stack that is
            // in there hand with the stack that was clicked on in the inventory
            else {
                inventoryPlayer.setItemStack(slotItemStack);
                _backpackContainer.putStackInSlot(slotIn.slotNumber, itemStack);
                LogChannel.INFO.log(UltraGuiScreenBackpack.class, "Swapping! Replacing item in inventory with item in the player's hand");
            }
        }
        // fall through default click handle, if we get here... then we either had an unknown click type
        // or the inventory that was involved was not the backpack inventory
        else {
            
            LogChannel.INFO.log(UltraGuiScreenBackpack.class, "Deferring! Passing slot click on to container");
            
            // Handle the slot click by passing it along to the container object
            boolean ignoreSlotClick = userShiftClicked && slotIn != null && slotIn.inventory == inventoryPlayer;
            if (!ignoreSlotClick) {
                _backpackContainer.slotClick(slotNumber, clickedButton, clickType, _thePlayer);
            }
            
            // Check if we were a hotbar clicked item?
            if (slotNumber >= 0 && slotNumber < HubbyConstants.HOTBAR_INVENTORY_SIZE) {
                ItemStack stack = ItemStack.copyItemStack(_thePlayer.inventory.mainInventory[slotNumber]);
                _backpackContainer.putStackInSlot(slotNumber, stack);
                
                // if we are in creative mode, then we can send inventory changes this way
                if (HubbyUtils.isCreativeMode()) {
                    Minecraft.getMinecraft().playerController.sendSlotPacket(stack, slotNumber + HubbyConstants.HOTBAR_INVENTORY_OFFSET);
                    _thePlayer.inventoryContainer.detectAndSendChanges();   
                }
                // we are not in creative mode, need to send changes an alternate way... hence the packet creator
                else {
                    HubbyClientPacketSenderHelper.sendPacketPlayerInventory(HubbyNetworkHelper.getDefaultChannelName(), stack, slotNumber + HubbyConstants.HOTBAR_INVENTORY_OFFSET);   
                }
            }
        }
    }
}