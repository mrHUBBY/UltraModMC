package com.hubby.ultra;

import java.util.List;

import com.hubby.events.HubbyEventPlayerInventory;
import com.hubby.events.HubbyEventSender;
import com.hubby.ultra.setup.UltraRegistry;
import com.hubby.utils.HubbyConstants.LightLevel;
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
     * Removes the light from the entity specified if there is one that exisits
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
     * Called when the player has a new ItemStack placed in their main inventory
     * @param slot - the slot within the inventory where the new <code>ItemStack</code> is being placed
     * @param oldStack - the old stack
     * @param newStack - the new stack
     */
    public static void onPlayerInventorySlotContentsChanged(Integer slot, ItemStack oldStack, ItemStack newStack) {
        String[] keys = HubbyEventPlayerInventory.getDefaultKeySet();
        Object[] params = new Object[] { slot, oldStack, newStack };
        HubbyEventSender.getInstance().notifyEvent(HubbyEventPlayerInventory.class, keys, params);
    }
    
    public static void onPlayerInventoryArmorChanged(Integer slot, ItemStack oldStack, ItemStack newStack) {
        oldStack = null;
    }
}
