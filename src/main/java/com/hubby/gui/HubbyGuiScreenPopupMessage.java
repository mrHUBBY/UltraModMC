package com.hubby.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.hubby.ultra.setup.UltraMod;
import com.hubby.utils.HubbySlicedResource;
import com.hubby.utils.HubbyUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * This class serves to popup a notification dialog
 * that can contain a short message along with a rendered
 * block or item for adornment
 * @author davidleistiko
 *
 */
public class HubbyGuiScreenPopupMessage extends GuiScreen {
 
    /**
     * This is the stretchable resource for the background
     */
    protected HubbySlicedResource _background;
    
    /**
     * The message to display in the popup
     */
    protected String _popupMessage;
    
    /**
     * This is the transformed popup message string that has
     * been formatted to fit within the width of this popup
     */
    protected String _fittedPopupMessage;
    
    /**
     * Members
     */
    protected static final int MAX_WIDTH = 160;
    protected static final int SIZE_X = 0;
    protected static final int SIZE_Y = 0;
    
    /**
     * Constructor
     */
    public HubbyGuiScreenPopupMessage(String message) {
        super();
        
        _popupMessage = message;
    }
    
    /**
     * The init function should handle initial setup of the gui
     */
    @Override
    public void initGui() {
        super.initGui();
        
        // create the stretchable background
        Map<String, Integer> constraints = new HashMap<String, Integer>();
        constraints.put(HubbySlicedResource.CONSTRAINT_CORNER_SIZE, 6);
        constraints.put(HubbySlicedResource.CONSTRAINT_MIN_WIDTH, 13);
        constraints.put(HubbySlicedResource.CONSTRAINT_MIN_HEIGHT, 13);
        constraints.put(HubbySlicedResource.CONSTRAINT_TEXTURE_WIDTH, 17);
        constraints.put(HubbySlicedResource.CONSTRAINT_TEXTURE_HEIGHT, 17);
        constraints.put(HubbySlicedResource.CONSTRAINT_RESOURCE_STEP, 17);
        constraints.put(HubbySlicedResource.CONSTRAINT_RESOURCE_LOCATION, 1);
        _background = new HubbySlicedResource(HubbyUtils.getResourceLocation(UltraMod.MOD_ID, "textures/gui/gui_popup_background.png"), constraints);
    }
    
    /**
     * Callback for when this gui is about to be closed
     */
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    /** 
     * Update loop called every frame, update the input fields
     */
    @Override
    public void updateScreen() {
        super.updateScreen();
    }
    
    /**
     * Callback for when a button is clicked
     * @param button - the gui button that was clicked
     */
    @Override
    public void actionPerformed(GuiButton button) {    
    }
    
    /**
     * Respond to mouse clicked event
     * @param mouseX - the x position of the mouse
     * @param mouseY - the y position of the mouse
     * @param mouseButton - the button of the mouse that was clicked
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    }
    
    /**
     * Callback for when a key is typed
     * @param keyChar - the typed character
     * @param keyCode - the corresponding code for the character
     */
    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
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
        _background.draw(this.mc, x1, y1, width, height, 0);
    }
    
    /**
     * Custom routine that draws all foreground elements that will
     * sit atop the textured background
     */
    public void drawForeground() {
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

