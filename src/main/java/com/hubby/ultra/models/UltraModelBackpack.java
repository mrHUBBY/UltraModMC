package com.hubby.ultra.models;

import com.hubby.shared.utils.HubbyMath;
import com.hubby.shared.utils.HubbyUtils;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * This class handles the rendering of the backpack on the player
 * when the player has a backpack equipped
 */
public class UltraModelBackpack extends ModelBiped {
    
    /**
     * Members
     */
    private ModelRenderer _backpackTop;
    private ModelRenderer _backpackBottom;
    private ModelRenderer _shape;

    /**
     * Constructor
     */
    public UltraModelBackpack() {
        // These models are not needed for the backpack
        // so we clear them out
        bipedBody = null;
        bipedHead = null;
        bipedHeadwear = null;
        bipedLeftArm = null;
        bipedLeftLeg = null;
        bipedRightArm = null;
        bipedRightLeg = null;

        textureWidth = 64;
        textureHeight = 32;

        // Init the models and renderers
        _backpackTop = new ModelRenderer(this, 0, 0);
        _backpackTop.addBox(-4F, 0F, 0F, 8, 4, 5);
        _backpackTop.setRotationPoint(0F, 0F, 2F);
        _backpackTop.setTextureSize(64, 32);
        //_backpackTop.mirror = true;
        _backpackTop.offsetY = 1.4f;
        setRotation(_backpackTop, 0.0f, 0.0f, (float)HubbyMath.toRadians(180.0));
        _backpackBottom = new ModelRenderer(this, 0, 10);
        _backpackBottom.addBox(-4F, 0F, 0F, 8, 7, 4);
        _backpackBottom.setRotationPoint(0F, 4F, 2F);
        _backpackBottom.setTextureSize(64, 32);
        //_backpackBottom.mirror = true;
        _backpackBottom.offsetY = 0.9f;
        setRotation(_backpackBottom, 0.0f, 0.0f, (float)HubbyMath.toRadians(180.0));
        _shape = new ModelRenderer(this, 9, 22);
        _shape.addBox(-1F, 0F, 0F, 2, 1, 1);
        _shape.setRotationPoint(0F, 3F, 7F);
        _shape.setTextureSize(64, 32);
        //_shape.mirror = true;
        _shape.offsetY = 0.95f;
        setRotation(_shape, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Handle rendering the entity with the backpack
     */
    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        GlStateManager.pushMatrix();
        
        // Calculate the angle for which to rotate the backpack
        // so that it matches the facing angle of the torso of the player
        EntityPlayer player = HubbyUtils.getClientPlayer();
        float angle = -player.rotationYaw + 180.0f;
        angle += (player.rotationYaw - player.renderYawOffset);
       
        // Apply rotation and draw backpack
        GlStateManager.rotate(angle, 0.0F, 1.0F, 0.0F);
        _backpackTop.render(f5);
        _backpackBottom.render(f5);
        _shape.render(f5);
        GlStateManager.popMatrix();
    }

    /**
     * Sets the rotation
     * @param model
     * @param x - the x rotation
     * @param y - the y rotation
     * @param z - the z rotation
     */
    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    /**
     * Set the rotation for this particular model
     */
    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity par6) {
        // No-op
    }
}
