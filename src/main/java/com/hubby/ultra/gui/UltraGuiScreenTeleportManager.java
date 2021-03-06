package com.hubby.ultra.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import com.hubby.ultra.UltraTeleportWaypoint;
import com.hubby.ultra.setup.UltraMod;
import com.hubby.utils.HubbyBlockResult;
import com.hubby.utils.HubbyColor;
import com.hubby.utils.HubbyColor.ColorMode;
import com.hubby.utils.HubbyInputFilter;
import com.hubby.utils.HubbyUtils;
import com.hubby.utils.HubbyUtils.GradientMode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;

/**
 * This class handles rendering the gui that allows players
 * to add waypoints and set their parameters
 * @author davidleistiko
 *
 */
public class UltraGuiScreenTeleportManager extends GuiScreen {

	/**
	 * Constant values
	 */
    protected static final int SAVE_WAYPOINT_BUTTON_ID = 0;
    protected static final int NAME_INPUT_FIELD_ID = 1;
    protected static final int COLOR_INPUT_FIELD_ID = 2;
    protected static final int SIZE_X = 176;
    protected static final int SIZE_Y = 45;
    protected static final HubbyInputFilter KEY_FILTER = new HubbyInputFilter("eE");
    protected static final String INPUT_PLACEHOLDER = "Enter name...";
    protected static final String COLOR_PLACEHOLDER = "Enter color...";

    /**
     * Members
     */
    protected GuiTextField _inputField = null;
    protected GuiTextField _colorField = null;
    protected ResourceLocation _backgroundResource = new ResourceLocation(HubbyUtils.getResourceLocation(UltraMod.MOD_ID, "textures/gui/gui_teleport_manager_background.png"));
    protected ResourceLocation _blankResource = new ResourceLocation(HubbyUtils.getResourceLocation(UltraMod.MOD_ID, "textures/gui/gui_blank.png"));
    protected RenderItem _renderItem = Minecraft.getMinecraft().getRenderItem();

    /**
     * Default constructor
     */
    public UltraGuiScreenTeleportManager() {

    }

    /**
     * The init function should handle initial setup of the gui
     */
    @Override
    public void initGui() {
        super.initGui();

        // get position
        int x1 = (width - SIZE_X) / 2;
        int y1 = (height - SIZE_Y) / 2;
        int inputX = (int) ((float) (x1 + 51));
        int inputY = (int) ((float) (y1 + 8));
        int colorX = (int) ((float) (x1 + 51));
        int colorY = (int) ((float) (y1 + 28));

        _inputField = new GuiTextField(NAME_INPUT_FIELD_ID, this.fontRendererObj, inputX, inputY, 78, 14);
        _inputField.setMaxStringLength(64);
        _inputField.setEnableBackgroundDrawing(false);
        _inputField.setFocused(false);
        _inputField.setText(INPUT_PLACEHOLDER);
        _inputField.setTextColor(HubbyColor.convertToMinecraftColor(0xFFFFFFFF));
        _inputField.setCanLoseFocus(true);
        _inputField.setFocused(false);
        _inputField.setCursorPosition(0);

        _colorField = new GuiTextField(COLOR_INPUT_FIELD_ID, this.fontRendererObj, colorX, colorY, 78, 14);
        _colorField.setMaxStringLength(16);
        _colorField.setEnableBackgroundDrawing(false);
        _colorField.setFocused(false);
        _colorField.setText(COLOR_PLACEHOLDER);
        _colorField.setTextColor(HubbyColor.convertToMinecraftColor(0xFFFFFFFF));
        _colorField.setCanLoseFocus(true);
        _colorField.setFocused(false);
        _colorField.setCursorPosition(0);

        int buttonX = (width - 128) / 2;
        int buttonY = (height - SIZE_Y) / 2 + SIZE_Y + 6;
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(SAVE_WAYPOINT_BUTTON_ID, buttonX, buttonY, 128, 20, "Save Waypoint"));
        
        // Button will remain disabled until the user enters valid text
        GuiButton btn = (GuiButton)this.buttonList.get(0);
        btn.enabled = false;
        
        Keyboard.enableRepeatEvents(true);
        
        // load the saved waypoint data to initialize this gui
        UltraTeleportWaypoint.load();
    }

    /**
     * Callback for when this gui is about to be closed
     */
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        
        // Save any changes that may have occurred while this gui was open
        UltraTeleportWaypoint.save();
    }

    /** 
     * Update loop called every frame, update the input fields
     */
    @Override
    public void updateScreen() {
        super.updateScreen();

        if (_inputField != null) {
            _inputField.updateCursorCounter();
        }
        if (_colorField != null) {
            _colorField.updateCursorCounter();
        }
    }

    /**
     * Callback for when a button is clicked
     * @param button - the gui button that was clicked
     */
    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
        // When the user clicks the save button, then
        // we get their selected color, name and the player's position
        // and we build a waypoint structure from that
        case SAVE_WAYPOINT_BUTTON_ID:
            String waypointName = _inputField.getText();
            String colorStr = _colorField.getText();
            HubbyColor color = HubbyColor.parseColor(colorStr);
            int mcColorValue = (int) color.getPackedColor(ColorMode.MINECRAFT);
            
            EntityPlayer thePlayer = Minecraft.getMinecraft().thePlayer;
            BlockPos pos = thePlayer.getPosition();
            float rotationY = thePlayer.rotationYaw;
            float rotationX = thePlayer.rotationPitch;
            UltraTeleportWaypoint waypoint = new UltraTeleportWaypoint(waypointName, mcColorValue, pos, rotationX, rotationY);
            break;
        }

        // Now that the user chose to add the waypoint we can close this gui screen
        Minecraft.getMinecraft().thePlayer.closeScreen();
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
        _colorField.mouseClicked(mouseX, mouseY, mouseButton);

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
        
        // if the color field has focus then remove the placeholder text
        if (_colorField.isFocused()) {
            if (_colorField.getText().equals(COLOR_PLACEHOLDER)) {
                _colorField.setText("");
            }
        }
        // otherwise, set the placeholder text if the current value is the emoty string
        else {
            if (_colorField.getText().equals("")) {
                _colorField.setText(COLOR_PLACEHOLDER);
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
        boolean addedColor = _colorField.textboxKeyTyped(keyChar, keyCode);

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
                _colorField.setFocused(true);
                
            }
            else if (_colorField.isFocused()) {
                _colorField.setFocused(false);
                _inputField.setFocused(true);
            }
            updateInputFields();   
            return;
        }
        
        // updates the color of the input field to let the user
        // know that they have entered a name that is already being used
        boolean isValid = isInputValid(_inputField.getText());
        GuiButton btn = (GuiButton)this.buttonList.get(0);
        btn.enabled = isValid && !_inputField.getText().equals(INPUT_PLACEHOLDER);
        
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
        return !UltraTeleportWaypoint.containsWaypoint(text) && text.length() > 0;
    }

    /**
     * Handles rendering this gui screen. The order of statements is
     * important as it determines the draw order and depth of each item
     * @param mouseX - the x position of the mouse
     * @param mouseY - the y position of the mouse
     * @param partialTicks - the elapsed time ?
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawForeground();

        _inputField.drawTextBox();
        _colorField.drawTextBox();
    }

    /**
     * Draws our custom background. Note that we don't call
     * the super method here as that would be redundant and it
     * would draw the default background on top of our custom background
     */
    @Override
    public void drawBackground(int tint) {
        int x1 = (width - SIZE_X) / 2;
        int y1 = (height - SIZE_Y) / 2;
        this.mc.renderEngine.bindTexture(_backgroundResource);
        HubbyUtils.drawTexturedRectHelper(0, x1, y1, SIZE_X, SIZE_Y, 0, 0, SIZE_X, SIZE_Y * (256 / 64));
    }

    /**
     * Custom routine that draws all foreground elements that will
     * sit atop the textured background
     */
    public void drawForeground() {

        // draw gui title
        String text = "Nitro Waypoint Creator";
        this.drawCenteredString(this.fontRendererObj, text, width / 2, (height - SIZE_Y) / 2 - 18, 0xFFFFFF);

        // This is needed in order to keep the strings and block icon
        // from rendering a shaded gray color
        RenderHelper.enableGUIStandardItemLighting();
        
        // draw name subtitle
        String nameTitle = "Name";
        this.fontRendererObj.drawStringWithShadow(nameTitle, (width - SIZE_X) / 2 + 9, (height - SIZE_Y) / 2 + 9, (int)HubbyColor.LIGHT_GREEN.getPackedColor(ColorMode.STANDARD));

        // draw color subtitle
        String colorTitle = "Color";
        this.fontRendererObj.drawStringWithShadow(colorTitle, (width - SIZE_X) / 2 + 9, (height - SIZE_Y) / 2 + 27, (int)HubbyColor.LIGHT_PURPLE.getPackedColor(ColorMode.STANDARD));

        // draw color selection
        String colorStr = _colorField.getText();
        HubbyColor color = HubbyColor.parseColor(colorStr);
        int colorValue = (int) color.getPackedColor(ColorMode.MINECRAFT);

        // draw color rect
        int left = (width - SIZE_X) / 2 + 148;
        int top = (height - SIZE_Y) / 2 + 29;
        int right = left + 16;
        int bottom = top + 9;
        HubbyColor colorOne = new HubbyColor((long) colorValue, ColorMode.MINECRAFT);
        HubbyColor colorTwo = new HubbyColor((long) colorValue, ColorMode.MINECRAFT);
        colorOne.setAlpha(1.0f);
        colorTwo.setAlpha(1.0f);
        Minecraft.getMinecraft().renderEngine.bindTexture(_blankResource);
        HubbyUtils.drawGradientRectHelper(GradientMode.HORIZONTAL, colorOne, colorTwo, left, top, right, bottom);

        // draw the block we are standing on
        int sX = (width - SIZE_X) / 2 + 148;
        int sY = (height - SIZE_Y) / 2 + 6;
        HubbyBlockResult result = HubbyUtils.findBlockUnderEntity(HubbyUtils.getClientPlayer());
        Item itemToRender = Item.getItemFromBlock(result.getBlock());        
        ItemStack stack = new ItemStack(itemToRender, 1, 0);
        
        _renderItem.renderItemIntoGUI(stack, sX, sY);
        _renderItem.renderItemOverlayIntoGUI(this.fontRendererObj, stack, sX, sY, "");
    }

    /**
     * Should this gui screen pause the game while it is open?
     * @return boolean - should the game pause?
     */
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
