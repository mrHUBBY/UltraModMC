package com.hubby.ultra.items;

import com.hubby.shared.utils.HubbyConstants.ArmorType;
import com.hubby.shared.utils.HubbyNamedObjectInterface;
import com.hubby.shared.utils.HubbyUtils;
import com.hubby.ultra.UltraConstants.BackpackType;
import com.hubby.ultra.setup.UltraMod;
import com.hubby.ultra.setup.UltraRegistry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * This class represents a custom armor piece that the player can
 * wear which is a backpack that comes in three different sizes. When
 * the backpack is equipped it acts as a global inventory that you
 * can use anywhere and at any time as long as it is equipped
 * @author davidleistiko
 */
public class UltraItemBackpackArmor extends ItemArmor implements HubbyNamedObjectInterface {

    /**
     * Members
     */
    private static final String NAME_PREFIX = "ultraItemBackpackArmor";
    private BackpackType _backpackType;
    
    /**
     * Constructor
     * @param material - the armor material
     * @param renderIndex - the render index
     * @param backpackType - the type of backpack
     */
    public UltraItemBackpackArmor(ArmorMaterial material, int renderIndex, BackpackType backpackType) {
        super(material, renderIndex, ArmorType.CHESTPLATE.getValue());
        
        // store the backpack type
        _backpackType = backpackType;
        
        // setup name and tab
        setUnlocalizedName(getName());
        setCreativeTab(UltraRegistry.ultraCreativeTab);
        
        // register the item
        HubbyUtils.registerNamedItem(UltraMod.MOD_ID, this);
    }

    /**
     * Returns the string name for this item
     */
    @Override
    public String getName() {
        return NAME_PREFIX + _backpackType.getSuffix();
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
        String modifier = _backpackType.getSuffix().toLowerCase();
        switch (armorType) {
        case CHESTPLATE:
            return HubbyUtils.getResourceLocation(UltraMod.MOD_ID, "textures/models/armor/ultra_backpack_armor_" + modifier + "_layer_1.png");
        case HELMET:
        case BOOTS:
        case LEGGINGS:
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
        return new CreativeTabs[] { CreativeTabs.tabInventory, UltraRegistry.ultraCreativeTab };
    }
}
