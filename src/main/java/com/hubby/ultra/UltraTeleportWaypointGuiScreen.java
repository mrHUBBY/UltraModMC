package com.hubby.ultra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.collect.ImmutableList;
import com.hubby.shared.utils.Utils;
import com.hubby.ultra.setup.UltraMod;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * This class handles rendering the gui that shows the user
 * all of the waypoints that they have saved and can teleport to
 * @author davidleistiko
 */
public class UltraTeleportWaypointGuiScreen extends GuiScreen {

	public final static int guiID = 100;
	public final static int xSize = 164;
	public final static int ySize = 116;
	public final static int xSizeScroll = 12;
	public final static int ySizeScroll = 15;
	public final static int ySizeScrollFull = 91;
	public final static int maxCellCount = 5;
	public final static int cellSizeX = 129;
	public final static int cellSizeY = 18;
	public static int cellStartX = 9;
	public static int cellStartY = 18;
	private final float textScale = 0.75f;
	private final static ResourceLocation rl = new ResourceLocation(Utils.getResourceLocation(UltraMod.MOD_ID, "textures/gui/gui_teleport_waypoint_background.png"));
	private final static ResourceLocation sliderResource = new ResourceLocation(Utils.getResourceLocation(UltraMod.MOD_ID, "textures/gui/gui_slider.png"));
	private final static ResourceLocation sliderFullResource = new ResourceLocation(Utils.getResourceLocation(UltraMod.MOD_ID, "textures/gui/gui_slider_91.png"));
	private final static ResourceLocation buttonOnResource = new ResourceLocation(Utils.getResourceLocation(UltraMod.MOD_ID, "textures/gui/gui_teleport_waypoint_delete_button_on.png"));
	private final static ResourceLocation buttonOffResource = new ResourceLocation(Utils.getResourceLocation(UltraMod.MOD_ID, "textures/gui/gui_teleport_waypoint_delete_button_off.png"));

	private int scrollOffsetX = 68;
	private int scrollOffsetY = -33;
	private int maxScrollValue = 77;
	private int curScrollValue = 0;
	private boolean scrollActive = false;
	private int lastScrollPosY = 0;
	private int selectedCell = -1;
	private int startCell = 0;
	private ScaledResolution scaledRes2 = null;
	private ArrayList<Boolean> selectedList = new ArrayList<Boolean>();
	private RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
	private GuiButton deleteButton = null;

	/**
	 * Init function that preps the gui
	 */
	@Override
	public void initGui() {
	    final int buttonSize = xSize - 10;

	    curScrollValue = 0;
	    buttonList.clear();
	    selectedList.clear();

	    deleteButton = new GuiButton(0, (width - buttonSize) / 2, (height + ySize) / 2 + 12, buttonSize, 20, "Delete Selected Waypoints");
	}

	/**
	 * Called when the user interacts with the gui by clicking a button
	 * @param button - the button that was clicked
	 */
	@Override
	public void actionPerformed(GuiButton button) {
       switch (button.id) {
       case 0:
            GuiYesNo confirmGui = new GuiYesNo(this, "Delete Waypoints", "Are you sure you want to delete the selected waypoints?", "Yes", "No", 0);
            confirmGui.setButtonDelay(5);
            this.mc.displayGuiScreen(confirmGui);
            break;
       }
	}

	/**
     * Handles the response from the GuiYesNo confirm screen, if par1 is true then
     * we know that the first button was pressed which in our case is the button "Yes",
     * so we delete the selected waypoints in that case
	 * @param result - did the user choose 'yes'
	 * @param id - the id of the button clicked
	 */
    @Override
    public void confirmClicked(boolean result, int id) {
        if (result == true) {
            deleteSelectedWaypoints();
        }

        // reset the current gui screen to be ourselves now that we are done
        // with the GuiYesNo dialog
        this.mc.displayGuiScreen(this);
    }

    /**
     * Iterates over all of the waypoints and removes any and all
     * that have been flagged for deletion
     */
	private void deleteSelectedWaypoints() {
	    int size = selectedList.size();
	    for (int i = selectedList.size() - 1; i >= 0; --i) {
            boolean selected = selectedList.get(i);
            if (selected) {
                selectedList.remove(i);
                UltraTeleportWaypoint.getWaypoints().remove(i);
            }
        }
	    UltraTeleportWaypoint.notufyHasChanges();
	}

	/**
	 * Handles rendering the gui to the screen
	 * @param x
	 * @param y
	 * @param f
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		if (scaledRes2 == null) {
			return;
		}

		drawBackground();
		drawScrollBar();
		super.drawScreen(mouseX, mouseY, partialTicks);
		drawWaypoints();

	    // draw the tooltip if we one
        UltraTeleportWaypoint nwp = getMouseOverWaypoint(mouseX, mouseY);
        if (nwp != null) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(nwp.getWaypointName());

            for (int k = 0; k < list.size(); ++k)
            {
                if (k == 0)
                {
                    list.set(k, EnumChatFormatting.BLUE + (String)list.get(k));
                }
                else
                {
                    list.set(k, EnumChatFormatting.GRAY + (String)list.get(k));
                }
            }

            drawToolTip(list, mouseX, mouseY, this.fontRendererObj);
        }
	}

	/**
	 * Helper routine to handle drawing the scroll-bar for this gui
	 */
	private void drawScrollBar() {

	    scrollOffsetX = 68;
	    scrollOffsetY = scaledRes2.getScaledHeight() % 2 == 1 ? -33 : -32;

	    ResourceLocation rl = getContentHeight() > getViewableHeight() ? sliderResource : sliderFullResource;
	    int ySizeScrollToUse = getContentHeight() > getViewableHeight() ? ySizeScroll : ySizeScrollFull;
	    int xTextureSize = getContentHeight() > getViewableHeight() ? 32 : 16;
	    int yTextureSize = getContentHeight() > getViewableHeight() ? 32 : 128;
	    curScrollValue = getContentHeight() > getViewableHeight() ? curScrollValue : 0;

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(rl);
		int x1 = (width - xSizeScroll) / 2;
		int y1 = (height - ySizeScroll) / 2;
		
		// draw the scroll bar
		Utils.drawTexturedRectHelper(0.0f, x1 + scrollOffsetX, y1 + scrollOffsetY + curScrollValue, xSizeScroll, ySizeScrollToUse, 0, 0, (256 / xTextureSize) * xSizeScroll, (256 / yTextureSize) * ySizeScrollToUse);
	}

	/**
	 * Returns the size of the contents that are being scrolled
	 * @return int - the height of the content being scrolled
	 */
	private int getContentHeight() {
		ImmutableList<UltraTeleportWaypoint> list = UltraTeleportWaypoint.getWaypoints();
		return list.size() * cellSizeY;
	}

	/**
	 * Returns the size of the scroll area
	 * @return int - the height of the scrollable area
	 */
	private int getViewableHeight() {
		return maxCellCount * cellSizeY;
	}

	/**
	 * Returns the index of the cell that should be rendered at the top
	 * @return int - the index of the top cell
	 */
	private int getStartCell() {
		int extraCells = (getContentHeight() - getViewableHeight()) / cellSizeY;
		if (getContentHeight() < getViewableHeight()) {
			extraCells = 0;
		}
		return (int) ((float) extraCells * ((float) curScrollValue / (float) maxScrollValue));
	}

	/**
	 * Helper routine that handles rendering the waypoint cells
	 */
	private void drawWaypoints() {
		int x1 = (width - xSize) / 2;
		int y1 = (height - ySize) / 2;

		float scissorFactor = (float) scaledRes2.getScaleFactor() / 2.0f;

		int glLeft = (int) ((x1 + 10) * 2.0f * scissorFactor);
		int glWidth = (int) ((xSize - (10 + 25)) * 2.0f * scissorFactor);
		int gluBottom = (int) ((this.height - y1 - ySize) * 2.0f * scissorFactor);
		int glHeight = (int) ((ySize) * 2.0f * scissorFactor);

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(glLeft, gluBottom, glWidth, glHeight);

		GL11.glPushMatrix();
		GL11.glScalef(textScale, textScale, textScale);

		startCell = getStartCell();
		cellStartX = (int) (((float) x1 + (float) 13) / textScale);
		cellStartY = (int) (((float) y1 + (float) 22) / textScale);

		int x2 = cellStartX;
		int y2 = cellStartY + (int) ((float) 0 * (float) cellSizeY / textScale);

		ImmutableList<UltraTeleportWaypoint> list = UltraTeleportWaypoint.getWaypoints();
		for (int i = 0; i < 2; ++i) {
    		for (int j = 0; j < list.size() && j < maxCellCount; ++j) {

    			int x = cellStartX;
    			int y = cellStartY + (int) ((float) j * (float) cellSizeY / textScale);

    			double posX = list.get(j + startCell).getPos().getX();
    			double posY = list.get(j + startCell).getPos().getY();
    			double posZ = list.get(j + startCell).getPos().getZ();

    			String strX = String.format("x: %4.1f", posX);
    			String strY = String.format("y: %4.1f", posY);
    			String strZ = String.format("z: %4.1f", posZ);
    			String text = strX + " " + strY + " " + strZ;

    			if (i == 0) {
    			    this.fontRendererObj.drawStringWithShadow(text, x + 18, y, list.get(j).getColor());

    			    boolean selected = j + startCell < selectedList.size() ? selectedList.get(j + startCell) : false;

    			    GL11.glPushMatrix();
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    			    this.mc.renderEngine.bindTexture(selected ? buttonOnResource : buttonOffResource);
    			    Utils.drawTexturedRectHelper(0, x + cellSizeX + 18, y - 3, 16, 16, 0, 0, 256, 256);
    			    GL11.glPopMatrix();
    			}
    			else if (i == 1) {
    				BlockPos pos = new BlockPos((int)posX, (int)posY - 1, (int)posZ);
    			    Block block = Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
        			if (block != null) {
        			    Item itemToRender = Item.getItemFromBlock(block);
        			    itemToRender = itemToRender != null ? itemToRender : Item.getItemFromBlock((Block)Block.blockRegistry.getObjectById(3));
        			    ItemStack is = new ItemStack(itemToRender, 1, 0);
        			    itemRender.renderItemAndEffectIntoGUI(is, x - 1, y - 3);
        	            itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, is, x - 1, y - 3, ""); // TODO: is the last param correct?
        			}
    			}
    		}
		}

		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	/**
	 * Draws the floating tool-tip to help users use this gui
	 * @param list - the list of text to render
	 * @param posX - the x position for this tool-tip
	 * @param posY - the y position for this tool-top
	 * @param fr - the font renderer object
	 */
	private void drawToolTip(List list, int posX, int posY, FontRenderer fr)
    {
        if (!list.isEmpty())
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                String s = (String)iterator.next();
                int l = fr.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }

            int j2 = posX + 12;
            int k2 = posY - 12;
            int i1 = 8;

            if (list.size() > 1)
            {
                i1 += 2 + (list.size() - 1) * 10;
            }

            if (j2 + k > this.width)
            {
                j2 -= 28 + k;
            }

            if (k2 + i1 + 6 > this.height)
            {
                k2 = this.height - i1 - 6;
            }

            this.zLevel = 300.0F;
            itemRender.zLevel = 300.0F;
            int j1 = -267386864;
            this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

            for (int i2 = 0; i2 < list.size(); ++i2)
            {
                String s1 = (String)list.get(i2);
                fr.drawStringWithShadow(s1, j2, k2, -1);

                if (i2 == 0)
                {
                    k2 += 2;
                }

                k2 += 10;
            }

            this.zLevel = 0.0F;
            itemRender.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
	}

	/**
	 * Respond to the mouse being clicked
	 */
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

		if (mouseButton == 0) {
			scrollActive = checkMouseScroll(mouseX, mouseY);
			lastScrollPosY = mouseY;
		}

		if (scrollActive) {
			return;
		}

		selectedCell = checkCellClick(mouseX, mouseY);
		if (selectedCell != -1) {
		    if (checkDeleteButton(selectedCell, mouseX, mouseY)) {
		        selectedCell = -1;
		    }
			return;
		}

		// call default method
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/**
	 * Respond to the mouse being dragged
	 * @param mouseX - the x position of the mouse
	 * @param mouseY - the y position of the mouse
	 * @param clickedMouseButton - the mouse button being held
	 * @param timeSinceLastClick - the time when the first click occurred
	 */
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if (scrollActive) {
			curScrollValue += mouseY - lastScrollPosY;
			curScrollValue = Math.max(0, Math.min(curScrollValue, maxScrollValue));
			lastScrollPosY = mouseY;
			return;
		}

		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}

	/**
	 * Query if the delete button was pressed
	 * @param cell - the index of the waypoint cell
	 * @param posX - the x position of the mouse
	 * @param posY - the y position of the mouse
	 * @return
	 */
	private boolean checkDeleteButton(int cell, int xPos, int yPos) {
        int x1 = cellStartX + cellSizeX + 18;
        int y1 = cellStartY + (int) ((float) cell * (float) cellSizeY / textScale) - 3;
        int x2 = x1 + 16;
        int y2 = y1 + 16;

        x1 *= textScale;
        y1 *= textScale;
        x2 *= textScale;
        y2 *= textScale;

        if (xPos >= x1 && xPos <= x2) {
            if (yPos >= y1 && yPos <= y2) {
                boolean selected = selectedList.get(startCell + cell);
                selectedList.set(startCell + cell, !selected);
                return true;
            }
        }

        return false;
	}

	/**
	 * Returns the number of selected waypoints
	 * @return int - the selected waypoint count
	 */
	private int getSelectedCount() {
	    int count = 0;
	    for (int i = 0; i < selectedList.size(); ++i) {
	        count += selectedList.get(i) ? 1 : 0;
	    }
	    return count;
	}

	/**
	 * Query to see if there is a waypoint cell that is under the mouse
	 * @param mouseX - the x position of the mouse
	 * @param mouseY - the y position of the mouse
	 * @return int - the index of the cell being hovered over (-1 if no cell)
	 */
	private int checkCellClick(int xPos, int yPos) {

		for (int i = 0; i < maxCellCount; ++i) {

			int x1 = ((width - xSize) / 2) + 16;
			int y1 = ((height - ySize) / 2) + 16 + (i * cellSizeY);
			int x2 = x1 + (xSize - 42);
			int y2 = y1 + cellSizeY;

			if (xPos >= x1 && xPos <= x2) {
				if (yPos >= y1 && yPos <= y2) {
					return i;
				}
			}
		}

		return -1;
	}

	/**
	 * Query to see if the user has clicked on the scroll-bar
	 * @param mouseX - the x position of the mouse
	 * @param mouseY - the y position of the mouse
	 * @return boolean - was the scroll bar activated
	 */
	private boolean checkMouseScroll(int mouseX, int mouseY) {
		int minX = (width - xSizeScroll) / 2 + scrollOffsetX;
		int maxX = minX + xSizeScroll;
		int minY = (height - ySizeScroll) / 2 + scrollOffsetY + curScrollValue;
		int maxY = minY + ySizeScroll;
		return Utils.isWithinRange(mouseX, minX, maxX) && Utils.isWithinRange(mouseY, minY, maxY);
	}

	/**
	 * Handles rendering the background of the gui
	 */
	private void drawBackground() {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(rl);
		String text = "Nitro Teleport Waypoints";
		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2;
		int strWidth = this.fontRendererObj.getStringWidth(text);

		this.drawTexturedModalRect(posX, posY, 0, 0, xSize, ySize);
		this.drawCenteredString(this.fontRendererObj, text, width / 2, posY - 17, 0xFFFFFF);
	}

	/**
	 * Catches the event of a key being pressed on this gui
	 * @param typedChar - the character pressed
	 * @param keyCode - the integer keyCode value
	 */
	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
	}

	/**
	 * Main update loop for this gui, called every frame
	 */
	@Override
	public void updateScreen() {
		super.updateScreen();
		scaledRes2 = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		this.width = scaledRes2.getScaledWidth();
		this.height = scaledRes2.getScaledHeight();

		// Handle when the player clicked on a cell
		if (selectedCell != -1) {
			// make sure we have our player
			// TODO:
			// How do you get the server player?
			if (UltraCommandHooks.theServerPlayer != null) {
				int cellIndex = startCell + selectedCell;
				if (cellIndex < UltraTeleportWaypoint.getWaypointCount()) {
				    UltraTeleportWaypoint p = UltraTeleportWaypoint.getWaypoints().get(cellIndex);
				    double posX = p.getPos().getX();
				    double posY = p.getPos().getY();
				    double posZ = p.getPos().getZ();
				    float yaw = p.getRotationY();
				    float pitch = p.getRotationX();

				    // TODO:
				    // Get the client world
				    //World w = NitroInterface.nitroClientWorld;
				    World world = Minecraft.getMinecraft().theWorld;
				    if (world != null) {
				        while (true) {
				        	BlockPos pos = new BlockPos(posX, posY, posZ);
				            if (world.isAirBlock(pos)) {
				                break;
				            }
				            posY += 1.0d;
				        }
				    }

				    ((EntityPlayerMP)UltraCommandHooks.theServerPlayer).playerNetServerHandler.setPlayerLocation(posX, posY, posZ, yaw, pitch);
				}
			}

			selectedCell = -1;
		}

		// update selected list
        for (int i = 0; i < UltraTeleportWaypoint.getWaypointCount(); ++i) {
            if (i >= selectedList.size()) {
                selectedList.add(false);
            }
        }

        // Only show delete button when we have something selected
        if (getSelectedCount() > 0) {
            this.buttonList.add(deleteButton);
        }
        else {
            this.buttonList.clear();
        }
	}

	/**
	 * Called when the gui is being closed
	 */
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
	}

	/**
	 * Should this gui pause the game when it is opened?
	 * @return boolean - should the game pause
	 */
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	/**
	 * Get the teleport waypoint that is under the specified mouse position
	 * @param mouseX - the x position of the mouse
	 * @param mouseY - the y position of the mouse
	 * @return NitroTeleportWaypoint - the waypoint (or null if none is being hovered over)
	 */
	private UltraTeleportWaypoint getMouseOverWaypoint(int mouseX, int mouseY) {

	    // calc pos
	    int posX = (width - xSize) / 2;
	    int posY = (height - ySize) / 2;

	    // check for the pos being in the header area
	    if (mouseY <= posY + 16) {
	        return null;
	    }

	    // add an offset to bring us to the y pos where the first cell is
	    posX += 9;
	    posY += 16;

	    // determine which row we are hovering over
	    int index = (mouseY - posY) / cellSizeY;
	    int start = this.getStartCell();
	    index += start;
	    
	    boolean inRangeX = Utils.isWithinRange(mouseX, posX, posX + cellSizeX);
	    if (index >= 0 && index < UltraTeleportWaypoint.getWaypoints().size() && inRangeX) {
	        return UltraTeleportWaypoint.getWaypoints().get(index);
	    }
	    return null;
	}
}
