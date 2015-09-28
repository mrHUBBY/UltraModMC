package com.hubby.gui;

import com.hubby.shared.utils.HubbyUtils;
import com.hubby.ultra.setup.UltraMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class HubbyStretchGuiButton extends GuiButton
{
    protected static final ResourceLocation buttonTextures = new ResourceLocation(HubbyUtils.getResourceLocation(UltraMod.MOD_ID, "textures/gui/gui_ultra_button.png"));
    protected static final int CORNER_SIZE = 3;
    protected static final int BUTTON_STEP = 8;
    protected static final int MIN_WIDTH = 8;
    protected static final int MIN_HEIGHT = 8;
    protected static final int TEXTURE_WIDTH = 32;
    protected static final int TEXTURE_HEIGHT = 64;

    /**
     * Constructor
     * @param buttonId - the id for the button
     * @param x - the x location
     * @param y - the y location
     * @param buttonText - the button text
     */
    public HubbyStretchGuiButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 60, 20, buttonText);
        width = Math.max(width, MIN_WIDTH);
        height = Math.max(height, MIN_HEIGHT);
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
    public HubbyStretchGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        width = Math.max(width, MIN_WIDTH);
        height = Math.max(height, MIN_HEIGHT);
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            FontRenderer var4 = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int var5 = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            
            // draw top-left corner
            drawScaledCustomSizeModalRect(this.xPosition, this.yPosition, 0, (var5 * BUTTON_STEP), CORNER_SIZE , CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            
            // draw bottom-left corner
            drawScaledCustomSizeModalRect(this.xPosition, this.yPosition + height - CORNER_SIZE, 0, (var5 * BUTTON_STEP) + BUTTON_STEP - CORNER_SIZE , CORNER_SIZE , CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            
            // draw top-right corner
            drawScaledCustomSizeModalRect(this.xPosition + width - CORNER_SIZE, this.yPosition, BUTTON_STEP - CORNER_SIZE, (var5 * BUTTON_STEP), CORNER_SIZE , CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            
            // draw bottom-right corner
            drawScaledCustomSizeModalRect(this.xPosition + width - CORNER_SIZE, this.yPosition + height - CORNER_SIZE, BUTTON_STEP - CORNER_SIZE, (var5 * BUTTON_STEP) + BUTTON_STEP - CORNER_SIZE, CORNER_SIZE , CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            
            // draw left edge
            drawScaledCustomSizeModalRect(this.xPosition, this.yPosition + CORNER_SIZE, 0, (var5 * BUTTON_STEP) + CORNER_SIZE, CORNER_SIZE , BUTTON_STEP - (CORNER_SIZE * 2), CORNER_SIZE, height - (CORNER_SIZE * 2), TEXTURE_WIDTH, TEXTURE_HEIGHT);
            
            // draw right edge
            drawScaledCustomSizeModalRect(this.xPosition + width - CORNER_SIZE, this.yPosition + CORNER_SIZE, BUTTON_STEP - CORNER_SIZE, (var5 * BUTTON_STEP) + CORNER_SIZE, CORNER_SIZE , BUTTON_STEP - (CORNER_SIZE * 2), CORNER_SIZE, height - (CORNER_SIZE * 2), TEXTURE_WIDTH, TEXTURE_HEIGHT);
            
            // draw top edge
            drawScaledCustomSizeModalRect(this.xPosition + CORNER_SIZE, this.yPosition, CORNER_SIZE, (var5 * BUTTON_STEP), BUTTON_STEP - (CORNER_SIZE * 2), CORNER_SIZE, width - (CORNER_SIZE * 2), CORNER_SIZE, TEXTURE_WIDTH, TEXTURE_HEIGHT);
           
            // draw bottom edge
            drawScaledCustomSizeModalRect(this.xPosition + CORNER_SIZE, this.yPosition + height - CORNER_SIZE, CORNER_SIZE, (var5 * BUTTON_STEP) + BUTTON_STEP - CORNER_SIZE, BUTTON_STEP - (CORNER_SIZE * 2), CORNER_SIZE, width - (CORNER_SIZE * 2), CORNER_SIZE, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            
            // draw the center
            drawScaledCustomSizeModalRect(this.xPosition + CORNER_SIZE, this.yPosition + CORNER_SIZE, CORNER_SIZE, (var5 * BUTTON_STEP) + CORNER_SIZE, BUTTON_STEP - (CORNER_SIZE * 2), BUTTON_STEP - (CORNER_SIZE * 2), width - (CORNER_SIZE * 2), height - (CORNER_SIZE * 2), TEXTURE_WIDTH, TEXTURE_HEIGHT);
            
            this.mouseDragged(mc, mouseX, mouseY);
            int var6 = 14737632;

            if (!this.enabled)
            {
                var6 = 10526880;
            }
            else if (this.hovered)
            {
                var6 = 16777120;
            }

            this.drawCenteredString(var4, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, var6);
        }
    }
}
