package com.hubby.ultra;

import java.util.List;

import com.hubby.events.HubbyEventPlayerInventory;
import com.hubby.events.HubbyEventSender;
import com.hubby.ultra.setup.UltraRegistry;
import com.hubby.utils.HubbyConstants;
import com.hubby.utils.HubbyConstants.LightLevel;
import com.hubby.utils.HubbyInstancedItem;
import com.hubby.utils.HubbyUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * This is a utility class specific to the Ultra Mod
 * @author davidleistiko
 */
public class UltraUtils {

    /**
     * Searches all fields declared within the passed in package and looks for any that are
     * instances of ItemArmor. For the found pieces of armor, each are applied to the entity
     * that was passed in.
     * @param package - the name of the package containing the ItemArmor instances as fields
     * @param klass - the class within the package to gather the fields for
     * @param entity - the entity to add the armor to
     * @param armor - the armor class we are looking for
     * @return boolean - were we successful?
     */
    public static boolean addFullUltraArmorToEntity(EntityLivingBase entity, final Class<? extends ItemArmor> armorClass) { 
        List<?> armorObjects = HubbyUtils.searchForFieldsOfType("com.hubby.ultra.setup", UltraRegistry.class, null, armorClass);
        for (Object item : armorObjects) {
            HubbyUtils.addArmorToEntity(entity, (ItemArmor)item);
        }
        return armorObjects.size() > 0;
    }
    
    /**
     * Attempts to attach a dynamic light to an entity
     * @param entity - the entity to attach the light to
     * @param level - the light level
     * @return boolean - was the attachment successful?
     */
    public static boolean attachLightToEntity(EntityLivingBase entity, LightLevel level) {
        if (entity != null && entity.isEntityAlive()) {
            UltraLightSourceNode foundNode = UltraLightHelper.getInstance().getLightForEntity(entity);
            if (foundNode != null) {
                foundNode.updateLightLevel(level);
                return true;
            }
            else {
                UltraLightSourceEntity lightEnt = new UltraLightSourceEntity(entity, level);
                UltraLightSourceNode node = new UltraLightSourceNode(lightEnt);
                UltraLightHelper.getInstance().addNodeLight(node);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Removes the light from the entity specified if there is one that exists
     * @param entity - the entity to remove the light from
     * @return boolean - did we remove anything?
     */
    public static boolean detachLightFromEntity(EntityLivingBase entity) {
        UltraLightSourceNode node = UltraLightHelper.getInstance().getLightForEntity(entity);
        if (node != null) {
            node.resetLightLevel();
            UltraLightHelper.getInstance().removeNodeLight(node);
            node = null;
            return true;
        }
        return false;
    }
    
    /**
     * Performs a simple search, looking for a light source that has an
     * attached entity the same as the entity passed in
     * @param ent - the entity to match
     * @return UltraLightSourceEntity - the matching light source (or null if no match was found)
     */
    public static UltraLightSourceNode findLightForEntity(Entity entity) {
        return UltraLightHelper.getInstance().getLightForEntity(entity);
    }
    
    /**
     * Returns if the entity in question has an attached light
     * @param entity - the entity to check
     * @return boolean - the result (true if the ent has a light)
     */
    public static boolean doesEntityHaveAttachedLight(Entity entity) {
        return UltraUtils.findLightForEntity(entity) != null;
    }
    
    /**
     * Called when the player has a new <code>ItemStack</code> placed in their main inventory
     * @param slot - the slot within the inventory where the new <code>ItemStack</code> is being placed
     * @param oldStack - the old stack
     * @param newStack - the new stack
     * @param inventory - this will be equal to either 'InventoryPlayer.mainInventory' or 'InventoryPlayer.armorInventory'
     * @bytecode UltraFMLTransformerInventoryPlayer.applyTransform
     */
    public static void onPlayerInventorySlotContentsChanged(Integer slot, ItemStack oldStack, ItemStack newStack, ItemStack[] inventory) {
        
        // It should be noted that if the inventory slot that changed has to do with armor, then we know that
        // the inventory parameter will have a length of 4 corresponding to number of available armor slots, while
        // if the inventory length is greater than that then we know that the changed slot has to do with the player's main
        // inventory which will always have a length of 36
        if (inventory.length == HubbyConstants.ARMOR_INVENTORY_SIZE) {
            onPlayerArmorInventorySlotContentsChanged(slot, oldStack, newStack, inventory);
        }
        else {
            String[] keys = HubbyEventPlayerInventory.getDefaultKeySet();
            Object[] params = new Object[] { slot, oldStack, newStack, false };
            HubbyEventSender.getInstance().notifyEvent(HubbyEventPlayerInventory.class, keys, params);
        }
    }
    
    /**
     * Called when the player has a new <code>ItemStack</code> placed in an armor slot
     * @param slot - the slot the armor was placed in
     * @param oldStack - the previous <code>ItemStack</code>
     * @param newStack - the newly updated <code>ItemStack</code>
     */
    public static void onPlayerArmorInventorySlotContentsChanged(Integer slot, ItemStack oldStack, ItemStack newStack, ItemStack[] inventory) {
        String[] keys = HubbyEventPlayerInventory.getDefaultKeySet();
        Object[] params = new Object[] { slot, oldStack, newStack, true };
        HubbyEventSender.getInstance().notifyEvent(HubbyEventPlayerInventory.class, keys, params);
    }
    
    /**
     * Called when the player attempts to place an <code>ItemStack</code> in their
     * inventory on the client. The function then determines which <code>ItemStack</code>,
     * if any, are valid for the specified slot and then returns the result.
     * It should be noted that this method is for creative mode only.
     * @param slot - the slot that the player is attempting to use
     * @param stack - the stack the player is attempting to place in the slot
     * @return ItemStack - the adjusted <code>ItemStack</code>
     * @bytecode UltraFMLTransformerPlayerControllerMP
     */
    public static ItemStack onItemStackPlacedInSlotAttempt(Integer slot, ItemStack stack) {
        
        // check for instanced items having reached their inventory limit
        if (stack != null && HubbyInstancedItem.class.isInstance(stack.getItem())) {
            HubbyInstancedItem instanceItem = (HubbyInstancedItem)stack.getItem();
            if (!instanceItem.containsItemInstance(stack) && instanceItem.hasReachedInstanceLimit()) {
                if (slot >= HubbyConstants.HOTBAR_INVENTORY_SIZE) {
                    slot -= HubbyConstants.HOTBAR_INVENTORY_OFFSET;
                }
                // Here we reset the slot contents to be null, effectively undoing what
                // the player just did by dropping the ItemStack into a slot
                HubbyUtils.getClientPlayer().inventory.setInventorySlotContents(slot, null);
                
                // Place the item that the player tried to drop in their inventory
                // back into the player's hand (ie the drag slot)
                HubbyUtils.getClientPlayer().inventory.setItemStack(stack.copy());
                
                // return null to be sent off to the server
                return null;
            }
        }
        
        // return the stack as is since it has been determined to be valid
        // considering the checks we have performed.
        return stack;
    }
}
