package com.hubby.gui;

import java.util.Map;

import com.hubby.utils.HubbyColor;
import com.hubby.utils.HubbyColor.ColorMode;
import com.hubby.utils.HubbySlicedResource;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class HubbyGuiButton extends GuiButton
{
    /**
     * The stretchable used for rendering the button
     */
    protected HubbySlicedResource _stretchable;
    
    /**
     * Colors for the button states
     */
    public static final HubbyColor DEFAULT_COLOR = new HubbyColor(14737632, ColorMode.MINECRAFT);
    public static final HubbyColor DISABLED_COLOR = new HubbyColor(10526880, ColorMode.MINECRAFT);
    public static final HubbyColor HOVERED_COLOR = new HubbyColor(16777120, ColorMode.MINECRAFT);

    /**
     * Constructor
     * @param buttonId - the id for the button
     * @param x - the x location
     * @param y - the y location
     * @param buttonText - the button text
     */
    public HubbyGuiButton(String resource, int buttonId, int x, int y, String buttonText, Map<String, Integer> constraints) {
        this(resource, buttonId, x, y, 60, 20, buttonText, constraints);
    }

    /**
     * Constructor
     * @param buttonId - the id for the button
     * @param x - the x location
     * @param y - the y location
     * @param widthIn - the button width
     * @param heightIn - the button height
     * @param buttonText - the button text
     */
    public HubbyGuiButton(String resource, int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, Map<String, Integer> constraints) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        
        // create the new stretchable to handle rendering our button
        _stretchable = new HubbySlicedResource(resource, constraints);
        
        // constrain the dimensions if they are too small
        width = Math.max(width, _stretchable.getConstraint(HubbySlicedResource.CONSTRAINT_MIN_WIDTH));
        height = Math.max(height, _stretchable.getConstraint(HubbySlicedResource.CONSTRAINT_MIN_HEIGHT));
    }

    /**
     * Draws this button to the screen.
     * @param mc - the minecraft instance
     * @param mouseX - the x-position for the mouse
     * @param mouseY - the y-position for the mouse
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        
        // if we are not visible then we have nothing to do...
        if (!this.visible) {
            return;
        }
                
        // determine hover state to determine the multiplier to use when drawing the stretchable
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int multiplier = this.getHoverState(this.hovered);
        
        // draw the stretchable background
        _stretchable.draw(mc, xPosition, yPosition, width, height, multiplier);
        
        // respond to the mouse being dragged...
        this.mouseDragged(mc, mouseX, mouseY);
        
        // determine the color of the button
        HubbyColor buttonColor = DEFAULT_COLOR;
        if (!enabled) {
            buttonColor = DISABLED_COLOR;
        }
        else if (hovered) {
            buttonColor = HOVERED_COLOR;
        }
        
        // draw the button text on top of the stretchable button background.
        this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, (int) buttonColor.getPackedColor(ColorMode.MINECRAFT));
    }
}
