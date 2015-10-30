package com.hubby.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import com.hubby.ultra.setup.UltraMod;
import com.hubby.utils.HubbyBlockResult;
import com.hubby.utils.HubbyColor;
import com.hubby.utils.HubbyColor.ColorMode;
import com.hubby.utils.HubbyConstants;
import com.hubby.utils.HubbyMath;
import com.hubby.utils.HubbyRefreshedObjectInterface;
import com.hubby.utils.HubbySize;
import com.hubby.utils.HubbySlicedResource;
import com.hubby.utils.HubbyUtils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

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
     * The original, unaltered message as it was passed in
     */
    protected String _originalMessage;

    /**
     * The formatted message to fit the popup width
     */
    protected String _formattedMessage;

    /**
     * A list of the formatted, fitted lines that were
     * calculated using the <code>_originalMessage</code> and
     * whatever value the user specified for the width
     */
    protected List<String> _formattedLines;

    /**
     * The width that the user would like to see the popup
     * to have. With this value we can parse the popup
     * message to make sure that it fits within the bounds
     */
    protected Integer _desiredWidth;

    /**
     * This determines a border area around the text that helps
     * to make the popup visually better since the string is not
     * crammed within the bounds of the background
     */
    protected Integer _innerBorder;

    /**
     * The popup dialog border
     */
    protected Integer _outerBorder;

    /**
     * The size that this popup should be
     */
    protected HubbySize<Integer> _size;

    /**
     * The color to render the text in
     */
    protected HubbyColor _textColor;

    /**
     * Use a drop shadow for the text?
     */
    protected boolean _useDropShadow;

    /**
     * Should the text be rendered centered in the popup?
     */
    protected boolean _centerText;

    /**
     * The item to render within the popup
     */
    protected Item _itemIcon;

    /**
     * The block to render within the popup
     */
    protected Block _blockIcon;
    
    /**
     * The block result for the block to render
     */
    protected HubbyBlockResult _blockResult;

    /**
     * The item renderer
     */
    protected RenderItem _itemRender = Minecraft.getMinecraft().getRenderItem();

    /**
     * The timer that is fired to auto-close the popup
     */
    protected Timer _autoCloseTimer;

    /**
     * The number of milliseconds before we close
     */
    protected Long _autoCloseMs;

    /**
     * Are we interpolating the alpha, yes, value > 0
     */
    protected Long _fadeTimeMs;

    /**
     * The current alpha value to use when fading the popup out
     */
    protected float _alpha;

    /**
     * Members
     */
    protected static final int MAX_WIDTH = 160;
    protected static final int SIZE_X = 0;
    protected static final int SIZE_Y = 0;

    /**
     * Constructor
     * @param message - the message to display
     * @param desiredWidth - the width they would like to see the popup have
     * @param innerBorder - the border around the text area
     * @param outerBorder - the border of the popup dialog image
     */
    public HubbyGuiScreenPopupMessage(String message, Integer desiredWidth, Integer innerBorder, Integer outerBorder) {
        super();
        _textColor = HubbyColor.WHITE;
        _useDropShadow = false;
        _centerText = false;
        _fadeTimeMs = -1L;
        _autoCloseMs = -1L;
        _alpha = 1.0f;
        setPopupMessage(message, desiredWidth, innerBorder, outerBorder);
    }

    /**
     * Takes the user's request and applies it to the popup message so that the
     * message is changed along with the popup size based on the new message and
     * the desired width plus spacing
     * @param message - the message to parse and display
     * @param desiredWidth - the desired width of the popup
     * @param innerBorder - the border around the text area
     * @param outerBorder - the border of the popup dialog image
     */
    public void setPopupMessage(String message, Integer desiredWidth, Integer innerBorder, Integer outerBorder) {
        _fadeTimeMs = -1L;
        _autoCloseMs = -1L;
        _alpha = 1.0f;
        _textColor.setAlpha(_alpha);
        _originalMessage = message;
        _desiredWidth = desiredWidth;
        _innerBorder = innerBorder;
        _outerBorder = outerBorder;
        _formattedLines = HubbyUtils.getStringLinesForWidth(_originalMessage, _desiredWidth);
        _formattedMessage = HubbyUtils.joinStrings(_formattedLines, "\n");
        _size = HubbyUtils.getStringDimensions(_formattedMessage, _innerBorder + _outerBorder);
    }

    /**
     * Sets the auto close time in which the popup will
     * close after the time expires
     * @param ms - the time in mills
     * @param fadeOut - should we fade out and interpolate the alpha
     */
    public void setAutoCloseTime(Long ms, boolean fadeOut) {
        _fadeTimeMs = fadeOut ? ms : -1L;
        _autoCloseMs = ms;
    }

    /**
     * Set the text color to use
     * @param color
     */
    public void setTextColor(HubbyColor color) {
        _textColor = color;
    }

    /**
     * Sets whether or not to render the text with a drop shadow
     * @param on - use drop shadow?
     */
    public void setDropShadow(boolean on) {
        _useDropShadow = on;
    }

    /**
     * Sets whether or not the text should be
     * rendered in the center of the popup
     * @param on
     */
    public void setCenterText(boolean on) {
        _centerText = on;
    }

    /**
     * Sets the item to use as icon
     * @param item - the item to use
     */
    public void setItemIcon(Item item) {
        _itemIcon = item;
    }

    /**
     * Set the block icon to use
     * @param block - the block to use
     */
    public void setBlockItemIcon(Block block) {
        _blockIcon = block;
        _itemIcon = Item.getItemFromBlock(_blockIcon);
    }

    /**
     * Use the default block icon which is defined as the
     * first non-air block underneath the player's current
     * position
     */
    public void useDefaultBlockItemIcon() {
        _blockResult = HubbyUtils.getStandOnBlock();
        _blockIcon = _blockResult.getBlock();
        _itemIcon = Item.getItemFromBlock(_blockIcon);
    }

    /**
     * Returns the max height to use
     * @return Integer - the height to use
     */
    public Integer getHeight() {
        Integer iconSize = HubbyConstants.ITEM_ICON_SIZE + 2 * HubbyConstants.ITEM_ICON_MARGIN;
        return Math.max(_size.getHeight(), _itemIcon != null ? iconSize : 0);
    }

    /**
     * Returns the width to use based on whether or not we have an icon
     * @return Integer - the width to use
     */
    public Integer getWidth() {
        // Here we only use 1 * the icon margin since we having spacing already accounted for
        // on both sides of the text so we only need to account for the spacing on the left of
        // the icon if we are using one
        Integer iconSize = HubbyConstants.ITEM_ICON_SIZE + HubbyConstants.ITEM_ICON_MARGIN + _outerBorder;
        return _size.getWidth() + (_itemIcon != null ? iconSize : 0);
    }

    /**
     * Are we currently on our way out?
     * @return boolean - are we auto closing?
     */
    public boolean isAutoClosing() {
        return _autoCloseMs >= 0L;
    }

    /**
     * Are we currently auto closing but fading out as well?
     * @return boolean - are we closing with a fade?
     */
    public boolean isAutoClosingWithFade() {
        return _autoCloseMs >= 0L && _fadeTimeMs >= 0L;
    }

    /**
     * Clears the item icon so that it will not show up this time
     * when we show the gui
     */
    public void clearItemIcon() {
        _blockIcon = null;
        _itemIcon = null;
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

        // are we fading out? If so, then re-calculate the new
        // alpha value that will be used for when rendering the
        // elements of this popup gui
        if (isAutoClosingWithFade()) {
            _fadeTimeMs -= HubbyRefreshedObjectInterface.getDeltaTime();
            _fadeTimeMs = _fadeTimeMs < 0L ? 0L : _fadeTimeMs;
            _alpha = (float) ((double) _fadeTimeMs / (double) _autoCloseMs);
            _textColor.setAlpha(_alpha);

            // if we have finished the interpolation then we can close
            // the screen and we can move on
            if (_fadeTimeMs == 0L) {
                _autoCloseMs = -1L;
                _fadeTimeMs = -1L;
                HubbyUtils.closeCurrentScreen();
            }
        }
        // if we are auto closing and our time is yet to expire, then
        // update the remaining time and if we go below zero then we
        // can close ourselves as the timeout has completed.
        else if (isAutoClosing()) {
            _autoCloseMs -= HubbyRefreshedObjectInterface.getDeltaTime();
            if (_autoCloseMs <= 0L) {
                _autoCloseMs = -1L;
                HubbyUtils.closeCurrentScreen();
            }
        }
    }

    /**s
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
        if (_alpha > 0.0f) {
            drawBackground(0);

            HubbyColor color = (HubbyColor) HubbyColor.WHITE.clone();
            color.setAlpha(_alpha);
            color.applyColorGL();
            super.drawScreen(mouseX, mouseY, partialTicks);

            // This is needed in order to keep the strings and block icon
            // from rendering a shaded gray color
            RenderHelper.enableGUIStandardItemLighting();
            drawForeground();
        }
        else {
            _alpha = 0.0f;
        }
    }

    /**
     * Draws our custom background. Note that we don't call
     * the super method here as that would be redundant and it
     * would draw the default background on top of our custom background
     */
    @Override
    public void drawBackground(int tint) {
        int x1 = (width - getWidth()) / 2;
        int y1 = (height - getHeight()) / 2;
        int iconSize = HubbyConstants.ITEM_ICON_SIZE + HubbyConstants.ITEM_ICON_MARGIN;
        int width = getWidth();
        int height = getHeight();

        HubbyColor color = (HubbyColor) HubbyColor.WHITE.clone();
        color.setAlpha(_alpha);
        color.applyColorGL();
        _background.draw(this.mc, x1, y1, width, height, 0);
    }

    /**
     * Custom routine that draws all foreground elements that will
     * sit atop the textured background, in this case we draw the popup
     * message and the icon if we have one
     */
    public void drawForeground() {
        FontRenderer fontRender = mc.fontRendererObj;

        int x1 = (width - getWidth()) / 2;
        int y1 = (height - getHeight()) / 2;
        int border = _innerBorder + _outerBorder;
        int iconX = x1 + HubbyConstants.ITEM_ICON_MARGIN + _outerBorder;
        int iconY = y1 + (int) ((float) (getHeight() - HubbyConstants.ITEM_ICON_SIZE) / 2.0f);

        // if the player wants to show an item icon then render that now
        if (_itemIcon != null) {
            //_itemIcon = HubbyUtils.searchForItem("diamond");
            this.useDefaultBlockItemIcon();

            //(ResourceLocation)Block.blockRegistry.getNameForObject(blockIn)
            ResourceLocation rl = HubbyUtils.getItemResourceLocation(_itemIcon, _blockResult);
            Integer iconSize = HubbyConstants.DEFAULT_ICON_SIZE;
            Integer uvSize = HubbyConstants.DEFAULT_UV_SIZE;
            mc.renderEngine.bindTexture(rl);
            HubbyUtils.drawTexturedRectHelper(1, iconX - 1, iconY - 3, iconSize, iconSize, 0, 0, uvSize, uvSize);

            //RenderHelper.enableGUIStandardItemLighting();
            //HubbyRenderItem.getInstance().useRenderColor(new HubbyColor(1.0f, 1.0f, 1.0f, _alpha), true);
            //HubbyRenderItem.getInstance().renderItemAndEffectIntoGUI(new ItemStack(_itemIcon, 1, 0), iconX - 1, iconY - 3);
            //HubbyRenderItem.getInstance().useRenderColor(null);
        }

        // add an additional offset if we are drawing an icon
        x1 += _itemIcon != null ? HubbyConstants.ITEM_ICON_MARGIN + HubbyConstants.ITEM_ICON_SIZE : 0;
        x1 += _itemIcon != null && !_centerText ? HubbyConstants.ITEM_ICON_MARGIN : 0;

        // render all strings
        if (_alpha >= HubbyMath.ALPHA_THRESHOLD) {
            for (String s : _formattedLines) {
                _textColor.setAlpha(_alpha);
                _textColor.applyColorGL();

                if (_centerText) {
                    int newX = (int) ((float) x1 + (getWidth() - fontRender.getStringWidth(s)) / 2.0f);
                    newX -= _itemIcon != null ? _outerBorder + _innerBorder : 0;
                    fontRender.drawString(s, newX, y1 + border, (int) _textColor.getPackedColor(ColorMode.MINECRAFT), _useDropShadow);
                }
                else {
                    fontRender.drawString(s, x1 + border, y1 + border, (int) _textColor.getPackedColor(ColorMode.MINECRAFT), _useDropShadow);
                }
                y1 += fontRender.FONT_HEIGHT;
            }
        }
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