package com.hubby.ultra;

import java.util.List;

import com.hubby.shared.utils.HubbyUtils;
import com.hubby.ultra.setup.UltraRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;

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
}
