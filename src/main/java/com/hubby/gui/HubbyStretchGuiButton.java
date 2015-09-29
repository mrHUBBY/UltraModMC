package com.hubby.gui;

import java.util.Map;

import com.hubby.ultra.setup.UltraMod;
import com.hubby.utils.HubbyUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class HubbyStretchGuiButton extends GuiButton
{
    protected ResourceLocation resourceLocation = new ResourceLocation(HubbyUtils.getResourceLocation(UltraMod.MOD_ID, "textures/gui/gui_ultra_button.png"));
    protected int cornerSize = 3;
    protected int buttonStep = 8;
    protected int minWidth = 8;
    protected int minHeight = 8;
    protected int textureWidth = 32;
    protected int textureHeight = 64;

    /**
     * Constructor
     * @param buttonId - the id for the button
     * @param x - the x location
     * @param y - the y location
     * @param buttonText - the button text
     */
    public HubbyStretchGuiButton(int buttonId, int x, int y, String buttonText, Map<String, Object> info) {
        this(buttonId, x, y, 60, 20, buttonText, info);
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
    public HubbyStretchGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, Map<String, Object> info) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);

        // read properties from the info map passed in for customization
        resourceLocation = new ResourceLocation((String)info.get("resourceLocation"));
        cornerSize = (Integer)info.get("cornerSize");
        buttonStep = (Integer)info.get("buttonStep");
        minWidth = (Integer)info.get("minWidth");
        minHeight = (Integer)info.get("minHeight");
        textureWidth = (Integer)info.get("textureWidth");
        textureHeight = (Integer)info.get("textureHeight");
        
        // constrain the dimensions if they are too small
        width = Math.max(width, minWidth);
        height = Math.max(height, minHeight);
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
            mc.getTextureManager().bindTexture(resourceLocation);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int var5 = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            
            // draw top-left corner
            Gui.drawScaledCustomSizeModalRect(this.xPosition, this.yPosition, 0, (var5 * buttonStep), cornerSize , cornerSize, cornerSize, cornerSize, textureWidth, textureHeight);
            
            // draw bottom-left corner
            Gui.drawScaledCustomSizeModalRect(this.xPosition, this.yPosition + height - cornerSize, 0, (var5 * buttonStep) + buttonStep - cornerSize , cornerSize , cornerSize, cornerSize, cornerSize, textureWidth, textureHeight);
            
            // draw top-right corner
            Gui.drawScaledCustomSizeModalRect(this.xPosition + width - cornerSize, this.yPosition, buttonStep - cornerSize, (var5 * buttonStep), cornerSize , cornerSize, cornerSize, cornerSize, textureWidth, textureHeight);
            
            // draw bottom-right corner
            Gui.drawScaledCustomSizeModalRect(this.xPosition + width - cornerSize, this.yPosition + height - cornerSize, buttonStep - cornerSize, (var5 * buttonStep) + buttonStep - cornerSize, cornerSize , cornerSize, cornerSize, cornerSize, textureWidth, textureHeight);
            
            // draw left edge
            Gui.drawScaledCustomSizeModalRect(this.xPosition, this.yPosition + cornerSize, 0, (var5 * buttonStep) + cornerSize, cornerSize , buttonStep - (cornerSize * 2), cornerSize, height - (cornerSize * 2), textureWidth, textureHeight);
            
            // draw right edge
            Gui.drawScaledCustomSizeModalRect(this.xPosition + width - cornerSize, this.yPosition + cornerSize, buttonStep - cornerSize, (var5 * buttonStep) + cornerSize, cornerSize , buttonStep - (cornerSize * 2), cornerSize, height - (cornerSize * 2), textureWidth, textureHeight);
            
            // draw top edge
            Gui.drawScaledCustomSizeModalRect(this.xPosition + cornerSize, this.yPosition, cornerSize, (var5 * buttonStep), buttonStep - (cornerSize * 2), cornerSize, width - (cornerSize * 2), cornerSize, textureWidth, textureHeight);
           
            // draw bottom edge
            Gui.drawScaledCustomSizeModalRect(this.xPosition + cornerSize, this.yPosition + height - cornerSize, cornerSize, (var5 * buttonStep) + buttonStep - cornerSize, buttonStep - (cornerSize * 2), cornerSize, width - (cornerSize * 2), cornerSize, textureWidth, textureHeight);
            
            // draw the center
            Gui.drawScaledCustomSizeModalRect(this.xPosition + cornerSize, this.yPosition + cornerSize, cornerSize, (var5 * buttonStep) + cornerSize, buttonStep - (cornerSize * 2), buttonStep - (cornerSize * 2), width - (cornerSize * 2), height - (cornerSize * 2), textureWidth, textureHeight);
            
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
