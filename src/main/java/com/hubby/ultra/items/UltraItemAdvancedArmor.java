package com.hubby.ultra.items;

import com.hubby.shared.utils.HubbyConstants.ArmorType;
import com.hubby.shared.utils.HubbyNamedObjectInterface;
import com.hubby.shared.utils.HubbyUtils;
import com.hubby.ultra.setup.UltraMod;
import com.hubby.ultra.setup.UltraRegistry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * A custom set of armor
 * @author davidleistiko
 */
public class UltraItemAdvancedArmor extends ItemArmor implements HubbyNamedObjectInterface {

    /**
     * The names of the armor pieces
     */
    private static final String[] NAMES = new String[] { "ultraItemAdvancedArmorHelmet", "ultraItemAdvancedArmorChestplate", "ultraItemAdvancedArmorLeggings", "ultraItemAdvancedArmorBoots" };
    
    /**
     * Constructor
     * @param material - the material for the armor
     * @param renderIndex - the renderIndex of the armor
     * @param armorType - the armor type
     */
    public UltraItemAdvancedArmor(ArmorMaterial material, int renderIndex, ArmorType armorType) {
        super(material, renderIndex, armorType.getValue());
        
        setUnlocalizedName(NAMES[armorType.getValue()]);
        setCreativeTab(UltraRegistry.ultraCreativeTab);
        
        HubbyUtils.registerNamedItem(UltraMod.MOD_ID, this);
    }
    
    /**
     * Returns the item name based on armor type
     * @param type - the type of armor to get the name for
     * @return String - the armor name
     */
    public String getName(ArmorType type) {
        return NAMES[type.getValue()];
    }

    /**
     * Returns the name based on the armor type value
     * @return String - the name of the item
     */
    @Override
    public String getName() {
        return NAMES[armorType];
    }
    
    /**
     * Returns the corresponding armor texture for the armor type determined by slot
     * @param stack - the <code>ItemStack</code> containing the armor
     * @param entity - the <code>Entity</code> wearing the armor
     * @param slot - the slot for the armor
     * @param type - the type string
     */
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        ArmorType armorType = ArmorType.values()[slot + 1];
        switch (armorType) {
        case HELMET:
        case CHESTPLATE:
        case BOOTS:
            return HubbyUtils.getResourceLocation(UltraMod.MOD_ID, "textures/models/armor/ultra_advanced_armor_layer_1.png");
        case LEGGINGS:
            return HubbyUtils.getResourceLocation(UltraMod.MOD_ID, "textures/models/armor/ultra_advanced_armor_layer_2.png");
        default:
            return null;
        }
    }
    
    /**
     * Returns all creative tabs that this armor will appear on
     * @return <code>CreativeTabs[]</code> the array of creative tabs
     */
    @Override
    public CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[] { CreativeTabs.tabCombat, UltraRegistry.ultraCreativeTab };
    }
}
