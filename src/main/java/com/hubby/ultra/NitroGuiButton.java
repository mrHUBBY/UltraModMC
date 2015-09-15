package com.hubby.ultra;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class NitroGuiButton extends GuiButton {

    public enum ButtonType {
        generic,
    };

    protected static final ResourceLocation rl = new ResourceLocation("nitroblock:textures/gui/nitro_buttons.png");
    protected int uvSize = 24;
    protected int zLevel = 0;
    protected ButtonType type = ButtonType.generic;

    // constructor
    public NitroGuiButton(int id, int xPos, int yPos, int width, int height, String text, ButtonType type) {
        super(id, xPos, yPos, width, height, text);
//        this.type = type;
    }

    @Override
    public void drawButton(Minecraft mc, int par1, int par2) {

//        if (this.visible == false) {
//            return;
//        }
//
//        FontRenderer fontrenderer = mc.fontRenderer;
//        mc.getTextureManager().bindTexture(rl);
//
//        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//        this.field_146123_n = par1 >= this.xPosition && par2 >= this.yPosition && par1 < this.xPosition + this.width && par2 < this.yPosition + this.height;
//        int k = this.getHoverState(this.field_146123_n);
//        GL11.glEnable(GL11.GL_BLEND);
//        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//
//        // determine offset to use when computing uvs and the object is disabled
//        int textureOffset = 0;
//        if (!this.enabled) {
//            textureOffset = uvSize;
//        }
//
//        // handle the button type to determine the uv's to draw
//        switch (type) {
//        case generic:
//            NitroInterface.drawTexturedModalRectEx(zLevel, xPosition, yPosition + 1, width, height, 0, 0, 24, 24);
//            break;
//        }
//
//        this.mouseDragged(mc, par1, par2);
//        int l = 14737632;
//
//        if (packedFGColour != 0)
//        {
//            l = packedFGColour;
//        }
//        else if (!this.enabled)
//        {
//            l = 10526880;
//        }
//        else if (this.field_146123_n)
//        {
//            l = 16777120;
//        }
//
//        this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + this.height / 2 - 3, l);
//        this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + this.height / 2 - 3, l);
    }
}
