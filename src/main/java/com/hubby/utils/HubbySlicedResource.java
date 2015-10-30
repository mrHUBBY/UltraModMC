package com.hubby.utils;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * The purpose of this class is to give the ability to have a
 * 9-sliced sprite that can be stretched to fit any dimensions
 * @author davidleistiko
 */
public class HubbySlicedResource {
    /**
     * The resource location specifying the texture to use for this
     * sliced resource.
     */
    protected ResourceLocation _resourceLocation = null;
    
    /**
     * Identifies the number of square pixels that are fixed in size 
     */
    protected int _cornerSize = 3;
    
    /**
     * Identifies the number of pixels to advance to the next resource
     * stop (used for handling various states)
     */
    protected int _resourceStep = 8;
    
    /**
     * The minimum width needed for the stretching to look right
     */
    protected int _minWidth = 8;
    
    /**
     * The minimum height needed for the stretching to look right
     */
    protected int _minHeight = 8;
    
    /**
     * The size of the texture width in pixels
     */
    protected int _textureWidth = 32;
    
    /**
     * The size of the texture height in pixels
     */
    protected int _textureHeight = 64;
    
    /**
     * A reference to the map object used to initialize this instance
     */
    private Map<String, Integer> _constraints;
    
    /**
     * The keys used for initializing this sliced resource
     */
    public static final String CONSTRAINT_CORNER_SIZE = "cornerSize";
    public static final String CONSTRAINT_RESOURCE_STEP = "resourceStep";
    public static final String CONSTRAINT_MIN_WIDTH = "minWidth";
    public static final String CONSTRAINT_MIN_HEIGHT = "minHeight";
    public static final String CONSTRAINT_TEXTURE_WIDTH = "textureWidth";
    public static final String CONSTRAINT_TEXTURE_HEIGHT = "textureHeight";
    public static final Integer INVALID_CONSTRAINT = -1;

    /**
     * Constructor, reads properties from the info map passed in for customization
     * @param info - the map containing the details about how we should stretch
     */
    public HubbySlicedResource(String resource, Map<String, Integer> constraints) {
        _constraints = constraints;
        _resourceLocation = new ResourceLocation(resource);
        _cornerSize = (Integer)_constraints.get(CONSTRAINT_CORNER_SIZE);
        _resourceStep = (Integer)_constraints.get(CONSTRAINT_RESOURCE_STEP);
        _minWidth = (Integer)_constraints.get(CONSTRAINT_MIN_WIDTH);
        _minHeight = (Integer)_constraints.get(CONSTRAINT_MIN_HEIGHT);
        _textureWidth = (Integer)_constraints.get(CONSTRAINT_TEXTURE_WIDTH);
        _textureHeight = (Integer)_constraints.get(CONSTRAINT_TEXTURE_HEIGHT);
    }
    
    /**
     * Handles drawing the stretchable resource
     * @param mc - the minecraft instance
     * @param xPos - the x-position to render at
     * @param yPos - the y-position to render at
     * @param multiplier - used to handle various states the resource can exist in
     */
    public void draw(Minecraft mc, Integer xPos, Integer yPos, Integer width, Integer height, Integer multiplier) {

        assert _resourceLocation != null : "[HubbySlicedResource] Cannot draw sliced resource, resource location is null!";
        assert width >= _minWidth : "[HubbySlicedResource] Attempting to draw stretchable with an invalid width!";
        assert height >= _minHeight : "[HubbySlicedResource] Attempting to draw stretchable with an invalid height";
        
        mc.getTextureManager().bindTexture(_resourceLocation);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        
        // draw top-left corner
        Gui.drawScaledCustomSizeModalRect(xPos, yPos, 0, (multiplier * _resourceStep), _cornerSize , _cornerSize, _cornerSize, _cornerSize, _textureWidth, _textureHeight);
        
        // draw bottom-left corner
        Gui.drawScaledCustomSizeModalRect(xPos, yPos + height - _cornerSize, 0, (multiplier * _resourceStep) + _resourceStep - _cornerSize , _cornerSize , _cornerSize, _cornerSize, _cornerSize, _textureWidth, _textureHeight);
        
        // draw top-right corner
        Gui.drawScaledCustomSizeModalRect(xPos + width - _cornerSize, yPos, _resourceStep - _cornerSize, (multiplier * _resourceStep), _cornerSize , _cornerSize, _cornerSize, _cornerSize, _textureWidth, _textureHeight);
        
        // draw bottom-right corner
        Gui.drawScaledCustomSizeModalRect(xPos + width - _cornerSize, yPos + height - _cornerSize, _resourceStep - _cornerSize, (multiplier * _resourceStep) + _resourceStep - _cornerSize, _cornerSize , _cornerSize, _cornerSize, _cornerSize, _textureWidth, _textureHeight);
        
        // draw left edge
        Gui.drawScaledCustomSizeModalRect(xPos, yPos + _cornerSize, 0, (multiplier * _resourceStep) + _cornerSize, _cornerSize , _resourceStep - (_cornerSize * 2), _cornerSize, height - (_cornerSize * 2), _textureWidth, _textureHeight);
        
        // draw right edge
        Gui.drawScaledCustomSizeModalRect(xPos + width - _cornerSize, yPos + _cornerSize, _resourceStep - _cornerSize, (multiplier * _resourceStep) + _cornerSize, _cornerSize , _resourceStep - (_cornerSize * 2), _cornerSize, height - (_cornerSize * 2), _textureWidth, _textureHeight);
        
        // draw top edge
        Gui.drawScaledCustomSizeModalRect(xPos + _cornerSize, yPos, _cornerSize, (multiplier * _resourceStep), _resourceStep - (_cornerSize * 2), _cornerSize, width - (_cornerSize * 2), _cornerSize, _textureWidth, _textureHeight);
       
        // draw bottom edge
        Gui.drawScaledCustomSizeModalRect(xPos + _cornerSize, yPos + height - _cornerSize, _cornerSize, (multiplier * _resourceStep) + _resourceStep - _cornerSize, _resourceStep - (_cornerSize * 2), _cornerSize, width - (_cornerSize * 2), _cornerSize, _textureWidth, _textureHeight);
        
        // draw the center
        Gui.drawScaledCustomSizeModalRect(xPos + _cornerSize, yPos + _cornerSize, _cornerSize, (multiplier * _resourceStep) + _cornerSize, _resourceStep - (_cornerSize * 2), _resourceStep - (_cornerSize * 2), width - (_cornerSize * 2), height - (_cornerSize * 2), _textureWidth, _textureHeight);
    }
    
    /**
     * Returns the resource location for this sliced resource
     * @return ResourceLocation - the location of the resource to use
     */
    public ResourceLocation getResourceLocation() {
        return _resourceLocation;
    }
    
    /**
     * Returns a specific constraint based on name
     * @param name - the name of the constraint to lookup
     * @return Integer - the constraint value (or -1 if an unknown constraint is specified)
     */
    public Integer getConstraint(String name) {
        if (_constraints.keySet().contains(name)) {
            return _constraints.get(name);
        }
        return INVALID_CONSTRAINT;
    }
}
