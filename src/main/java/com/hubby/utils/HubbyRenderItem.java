package com.hubby.utils;

import java.util.Iterator;
import java.util.List;

import com.hubby.utils.HubbyColor.ColorMode;
import com.hubby.utils.HubbyConstants.LogChannel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3i;

/**
 * This class is more or less a copy of the RenderItem class found in 
 * Minecraft. It allows for the rendering of items and blocks for the
 * inventory or other GUIs that would use items.
 * @author davidleistiko
 */
public class HubbyRenderItem implements IResourceManagerReloadListener {

    /**
     * Members
     */
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final HubbyRenderItem INSTANCE = new HubbyRenderItem();
    
    private boolean field_175058_l = true;
    private final ItemModelMesher itemModelMesher;
    private final TextureManager textureManager;
    private HubbyColor _renderColor;
    private boolean _useRenderColorAlphaOnly;
    
    public float zLevel;
    public static float debugItemOffsetX = 0.0F;
    public static float debugItemOffsetY = 0.0F;
    public static float debugItemOffsetZ = 0.0F;
    public static float debugItemRotationOffsetX = 0.0F;
    public static float debugItemRotationOffsetY = 0.0F;
    public static float debugItemRotationOffsetZ = 0.0F;
    public static float debugItemScaleX = 0.0F;
    public static float debugItemScaleY = 0.0F;
    public static float debugItemScaleZ = 0.0F;
    
    /**
     * Access to the singleton object
     * @return HubbyRenderItem - the render item instance
     */
    public static HubbyRenderItem getInstance() {
        return INSTANCE;
    }
    
    /**
     * Applies the vanilla transform for Minecraft
     * @param transform
     */
    public static void applyVanillaTransform(ItemTransformVec3f transform) {
        if (transform != ItemTransformVec3f.DEFAULT) {
            GlStateManager.translate(transform.translation.x + debugItemOffsetX, transform.translation.y + debugItemOffsetY, transform.translation.z + debugItemOffsetZ);
            GlStateManager.rotate(transform.rotation.y + debugItemRotationOffsetY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(transform.rotation.x + debugItemRotationOffsetX, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(transform.rotation.z + debugItemRotationOffsetZ, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(transform.scale.x + debugItemScaleX, transform.scale.y + debugItemScaleY, transform.scale.z + debugItemScaleZ);
        }
    }
    
    /**
     * Constructor
     * @param textureManager - the textureManager
     * @param modelManager - the modelManager
     */
    public HubbyRenderItem() {
        textureManager = Minecraft.getMinecraft().getTextureManager();;
        itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        
        // NOTE:
        // I don't think we need to do this since we are just referencing the 'itemModelMesher' that belongs
        // to the 'RenderItem' class that you can access from the 'Minecraft' object, thus if we were to register
        // as a reload listener here then that would just be redundant
        // add ourselves as a resource listener
        //((SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
    }
    
    /**
     * Handle when the resource manager gets reloaded.
     * @param resourceManager - the updated resource manager
     */
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        itemModelMesher.rebuildCache();
    }
    
    /**
     * Sets the render color to use
     * @param color - the color to use
     */
    public void useRenderColor(HubbyColor color) {
        useRenderColor(color, false);
    }
    
    /**
     * Sets or unsets the color we should use when rendering any item. If the
     * user sets the color to <code>null</code> then the default handling of
     * color will be used instead.
     * @param color - the color to use
     */
    public void useRenderColor(HubbyColor color, boolean alphaOnly) {
        _renderColor = color != null ? (HubbyColor)color.clone() : null;
        _useRenderColorAlphaOnly = alphaOnly;
    }
    
    /**
     * Returns the model mesher for items
     * @return ItemModelMesher - returns access to this object
     */
    public ItemModelMesher getItemModelMesher() {
        return itemModelMesher;
    }
    
    /**
     * Returns whether or not the <code>Item</code> should be in 3D or not
     * @param stack - the stack containing the <code>Item</code> to render
     * @return boolean - should we render in 3D?
     */
    public boolean shouldRenderItemIn3D(ItemStack stack) {
        IBakedModel ibakedmodel = itemModelMesher.getItemModel(stack);
        return ibakedmodel == null ? false : ibakedmodel.isGui3d();
    }

    
    /**
     * Utility function for rendering an <code>Item</code> inside of a 
     * gui screen, like rendering items for the player's inventory
     * @param stack - the <code>ItemStack</code> with the item to render
     * @param x - the x position to render at
     * @param y - the y position to render at
     */
    public void renderItemIntoGUI(ItemStack stack, int x, int y) {
        IBakedModel ibakedmodel = this.itemModelMesher.getItemModel(stack);
        GlStateManager.pushMatrix();
        textureManager.bindTexture(TextureMap.locationBlocksTexture);
        textureManager.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        applyColor();
        setupGuiTransform(x, y, ibakedmodel.isGui3d());
        ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GUI);
        renderItem(stack, ibakedmodel);
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        textureManager.bindTexture(TextureMap.locationBlocksTexture);
        textureManager.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
    }
    
    /**
     * Renders both the item and the item's effect into the current gui
     * @param stack - the stack containing the <code>Item</code> we want to render
     * @param xPosition - the x position to render at
     * @param yPosition - the y position to render at
     */
    public void renderItemAndEffectIntoGUI(final ItemStack stack, int xPosition, int yPosition) {
        
        // Don't do anything if we don't need to
        if (stack == null || stack.getItem() == null) {
            return;
        }
        
        // offset the z-level so we draw on top
        zLevel += 50.0F;

        try {
            renderItemIntoGUI(stack, xPosition, yPosition);
        }
        catch (Throwable throwable) {
            LogChannel.ERROR.log(HubbyRenderItem.class, "Failed to render item %s into gui!", stack.getItem().getUnlocalizedName());
        }

        // offset the z-level back to where it was so others
        // draw properly after we are done drawing ourselves
        zLevel -= 50.0F;
    }
    
    /**
     * Renders the overlays for the item specified
     * @param fr - the font renderer to use for text
     * @param stack - the stack with the <code>Item</code> we care about
     * @param xPosition - the x position for the render
     * @param yPosition - the y position for the render
     */
    public void renderItemOverlays(FontRenderer fr, ItemStack stack, int xPosition, int yPosition) {
        renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, (String)null);
    }
    
    /**
     * Renders the <code>Item</code> model for that which is contained in the stack
     * @param stack - the stack containing the <code>Item</code> we want to render
     */
    public void renderItemModel(ItemStack stack) {
        IBakedModel ibakedmodel = this.itemModelMesher.getItemModel(stack);
        renderItemModelTransform(stack, ibakedmodel, ItemCameraTransforms.TransformType.NONE);
    }
    
    /**
     * Renders the overlay for the item specified, such as stack size and durability
     * @param fr - the font renderer to use
     * @param stack - the stack containing the <code>Item</code> to write overlay for
     * @param xPosition - the x position for render
     * @param yPosition - the y position for render
     * @param text - text to draw as overlay
     */
    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text) {
        // Don't do anything if we don't need to
        if (stack == null || stack.getItem() == null) {
            return;
        }
        
        // Handle stack size other than 1
        if (stack.stackSize != 1 || text != null) {
            String s1 = text == null ? String.valueOf(stack.stackSize) : text;

            if (text == null && stack.stackSize < 1) {
                s1 = EnumChatFormatting.RED + String.valueOf(stack.stackSize);
            }

            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableBlend();
            fr.drawStringWithShadow(s1, (float)(xPosition + 19 - 2 - fr.getStringWidth(s1)), (float)(yPosition + 6 + 3), 16777215);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }

        // Is there a durability bar to show for the item?
        if (stack.getItem().showDurabilityBar(stack)) {
            double health = stack.getItem().getDurabilityForDisplay(stack);
            int j1 = (int)Math.round(13.0D - health * 13.0D);
            int k = (int)Math.round(255.0D - health * 255.0D);
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            int l = 255 - k << 16 | k << 8;
            int i1 = (255 - k) / 4 << 16 | 16128;
            drawRect(worldrenderer, xPosition + 2, yPosition + 13, 13, 2, 0);
            drawRect(worldrenderer, xPosition + 2, yPosition + 13, 12, 1, i1);
            drawRect(worldrenderer, xPosition + 2, yPosition + 13, j1, 1, l);
            //GlStateManager.enableBlend(); // Forge: Disable Bled because it screws with a lot of things down the line.
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }
    }
    
    /**
     * Renders an item based on the model passed in
     * @param stack - the <code>ItemStack</code> containing the item we want to render
     * @param model - the model refers to the resource needed to render the item
     */
    public void renderItem(ItemStack stack, IBakedModel model) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);

        if (model.isBuiltInRenderer()) {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            applyColor();
            GlStateManager.enableRescaleNormal();
            TileEntityItemStackRenderer.instance.renderByItem(stack);
        }
        else {
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            renderModel(model, stack);

            if (stack.hasEffect()) {
                renderEffect(model);
            }
        }

        GlStateManager.popMatrix();
    }
    
    /**
     * Render the item model for an entity
     * @param stack - the stack containing the <code>Item</code> to render
     * @param entityToRenderFor - the entity who we are rendering for
     * @param cameraTransformType - the transform type to apply
     */
    public void renderItemModelForEntity(ItemStack stack, EntityLivingBase entityToRenderFor, ItemCameraTransforms.TransformType cameraTransformType) {
        
        // get the model for the item
        IBakedModel ibakedmodel = itemModelMesher.getItemModel(stack);

        // Are we the player?
        if (entityToRenderFor instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entityToRenderFor;
            Item item = stack.getItem();
            ModelResourceLocation modelresourcelocation = null;

            if (item == Items.fishing_rod && entityplayer.fishEntity != null) {
                modelresourcelocation = new ModelResourceLocation("fishing_rod_cast", "inventory");
            }
            else if (item == Items.bow && entityplayer.getItemInUse() != null) {
                int i = stack.getMaxItemUseDuration() - entityplayer.getItemInUseCount();

                if (i >= 18) {
                    modelresourcelocation = new ModelResourceLocation("bow_pulling_2", "inventory");
                }
                else if (i > 13) {
                    modelresourcelocation = new ModelResourceLocation("bow_pulling_1", "inventory");
                }
                else if (i > 0) {
                    modelresourcelocation = new ModelResourceLocation("bow_pulling_0", "inventory");
                }
            }
            else {
                modelresourcelocation = item.getModel(stack, entityplayer, entityplayer.getItemInUseCount());
            }

            // get the updated model using the resource location
            if (modelresourcelocation != null) {
                ibakedmodel = this.itemModelMesher.getModelManager().getModel(modelresourcelocation);
            }
        }

        // render the model with the transform
        renderItemModelTransform(stack, ibakedmodel, cameraTransformType);
    }
    
    /**
     * Sets the current color that will affect the item rendering
     */
    protected void applyColor() {
        if (_renderColor != null) {
            if (_useRenderColorAlphaOnly) {
                HubbyColor color = new HubbyColor(1.0f, 1.0f, 1.0f, _renderColor.getAlpha());
                color.applyColorStateManager();
            }
            else {
                _renderColor.applyColorStateManager();
            }
        }
        else {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
    
    /**
     * Returns the color to use for rendering
     * @param defaultColor - the default color value if <code>_renderColor</code> is null
     * @return int - the color value to use
     */
    protected int getRenderColor(int defaultColor) {
        if (_renderColor != null) {
            if (_useRenderColorAlphaOnly) {
                HubbyColor col = new HubbyColor(defaultColor, ColorMode.MINECRAFT);
                col.setAlpha(_renderColor.getAlpha());
                return (int)col.getPackedColor(ColorMode.MINECRAFT);
            }
            else {
                return (int)_renderColor.getPackedColor(ColorMode.MINECRAFT);
            }
        }
        return defaultColor;
    }
    
    /**
     * Applies the transform passed in
     * @param transform - the transform to apply
     */
    protected void applyTransform(ItemTransformVec3f transform) {
        applyVanillaTransform(transform);
    }

    /**
     * Render the <code>Item</code> using the transform
     * @param stack - the stack containing the <code>Item</code> to render model for
     * @param model - the model to render
     * @param cameraTransformType - the tranform type
     */
    protected void renderItemModelTransform(ItemStack stack, IBakedModel model, ItemCameraTransforms.TransformType cameraTransformType) {
        textureManager.bindTexture(TextureMap.locationBlocksTexture);
        textureManager.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
        preTransform(stack);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();

        model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, cameraTransformType);

        renderItem(stack, model);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        textureManager.bindTexture(TextureMap.locationBlocksTexture);
        textureManager.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
    }
    
    /**
     * Applies the transform needed just before we perform the render on the item
     * @param stack - the stack containing the <code>Item</code> to do the transform for
     */
    private void preTransform(ItemStack stack) {
        IBakedModel ibakedmodel = this.itemModelMesher.getItemModel(stack);
        Item item = stack.getItem();
        
        if (item != null) {
            boolean flag = ibakedmodel.isGui3d();
            if (!flag) {
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
            }
            applyColor();
        }
    }
    
    /**
     * Draws the rectangle with the params specified
     * @param renderer - the renderer to do the render
     * @param x - the x position
     * @param y - the y position
     * @param width - the width of the rect
     * @param height - the height of the rect
     * @param color - the color to use for the render
     */
    private void drawRect(WorldRenderer renderer, int x, int y, int width, int height, int color) {
        renderer.startDrawingQuads();
        renderer.setColorOpaque_I(color);
        renderer.addVertex((double)(x + 0), (double)(y + 0), 0.0D);
        renderer.addVertex((double)(x + 0), (double)(y + height), 0.0D);
        renderer.addVertex((double)(x + width), (double)(y + height), 0.0D);
        renderer.addVertex((double)(x + width), (double)(y + 0), 0.0D);
        Tessellator.getInstance().draw();
    }
    
    /**
     * Renders the model using the specified color
     * @param model - the model to render
     * @param color - the color to use for rendering
     */
    private void renderModel(IBakedModel model, int color) {
        renderModel(model, color, (ItemStack)null);
    }

    /**
     * Renders the model for the item given along with the specified color
     * @param model - the model to render
     * @param color - the color to use for rendering
     * @param stack - the stack containing the <code>Item</code> to render
     */
    private void renderModel(IBakedModel model, int color, ItemStack stack) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.startDrawingQuads();
        worldrenderer.setVertexFormat(DefaultVertexFormats.ITEM);
        EnumFacing[] aenumfacing = EnumFacing.values();
        int j = aenumfacing.length;

        for (int k = 0; k < j; ++k) {
            EnumFacing enumfacing = aenumfacing[k];
            renderQuads(worldrenderer, model.getFaceQuads(enumfacing), color, stack);
        }

        renderQuads(worldrenderer, model.getGeneralQuads(), color, stack);
        tessellator.draw();
    }
    
    /**
     * Renders a single quad, the one that is past in to this method
     * @param renderer - the renderer to use for rendering the quad
     * @param quad - the quad we want to render
     * @param color - the color to use for rendering
     */
    private void renderQuad(WorldRenderer renderer, BakedQuad quad, int color) {
        color = getRenderColor(color);
        renderer.addVertexData(quad.getVertexData());
        if(quad instanceof net.minecraftforge.client.model.IColoredBakedQuad) {
            net.minecraftforge.client.ForgeHooksClient.putQuadColor(renderer, quad, color);
        }
        else {
            renderer.putColor4(color);
        }
        putQuadNormal(renderer, quad);
    }
    
    /**
     * Adds the quad's normal to the list on the renderer
     * @param renderer - the renderer to add the quad to
     * @param quad - the quad we want to store in the renderer
     */
    private void putQuadNormal(WorldRenderer renderer, BakedQuad quad) {
        Vec3i vec3i = quad.getFace().getDirectionVec();
        renderer.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
    }

    /**
     * Renders the list of quads passed in that correspond to the <code>ItemStack</code>
     * being passed in as well.
     * @param renderer - the renderer to perform the render
     * @param quads - the list of quads to render
     * @param color - the color to use for rendering
     * @param stack - the stack containing the <code>Item</code> we want to render
     */
    private void renderQuads(WorldRenderer renderer, List quads, int color, ItemStack stack) {
        boolean flag = color == -1 && stack != null;
        BakedQuad bakedquad;
        int j;

        for (Iterator iterator = quads.iterator(); iterator.hasNext(); this.renderQuad(renderer, bakedquad, j)) {
            bakedquad = (BakedQuad)iterator.next();
            j = color;

            if (flag && bakedquad.hasTintIndex()) {
                j = stack.getItem().getColorFromItemStack(stack, bakedquad.getTintIndex());

                if (EntityRenderer.anaglyphEnable) {
                    j = TextureUtil.anaglyphColor(j);
                }

                j |= -16777216;
            }
        }
    }
    
    /**
     * Renders an effect overlay for the model passed in
     * @param model - the model to render the effect for
     */
    private void renderEffect(IBakedModel model) {
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(768, 1);
        textureManager.bindTexture(RES_ITEM_GLINT);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        renderModel(model, -8372020);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        renderModel(model, -8372020);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        textureManager.bindTexture(TextureMap.locationBlocksTexture);
    }
    
    /**
     * Pass through method for rendering item model
     * @param model - the model to render
     * @param stack - the <code>ItemStack</code> containing the item
     */
    private void renderModel(IBakedModel model, ItemStack stack) {
        renderModel(model, -1, stack);
    }
    
    /**
     * Sets up the GL state for rendering an item into a gui
     * @param xPosition - the x position for the translation
     * @param yPosition - the y position for the translation
     * @param isGui3d - is it 3D, like rendering a block as an item?
     */
    private void setupGuiTransform(int xPosition, int yPosition, boolean isGui3d) {
        GlStateManager.translate((float)xPosition, (float)yPosition, 100.0F + zLevel);
        GlStateManager.translate(8.0F, 8.0F, 0.0F);
        GlStateManager.scale(1.0F, 1.0F, -1.0F);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);

        if (isGui3d) {
            GlStateManager.scale(40.0F, 40.0F, 40.0F);
            GlStateManager.rotate(210.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.enableLighting();
        }
        else {
            GlStateManager.scale(64.0F, 64.0F, 64.0F);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.disableLighting();
        }
    }
}
