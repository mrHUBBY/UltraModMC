package com.hubby.ultra.items;

import java.util.List;

import com.hubby.ultra.setup.UltraMod;
import com.hubby.ultra.setup.UltraRegistry;
import com.hubby.utils.HubbyNamedObjectInterface;
import com.hubby.utils.HubbyUtils;
import com.hubby.utils.HubbyConstants.ArmorType;

import net.minecraft.item.ItemArmor;

/**
 * This class serves as a custom option where the names can be dynamic based
 * on the type that you want to create
 * @author davidleistiko
 */
public class UltraItemCustomArmor extends ItemArmor implements HubbyNamedObjectInterface {

    private static final String[] NAMES = new String[ArmorType.validLength()];
    
    /**
     * Constructor
     * @param material
     * @param renderIndex
     * @param armorType
     */
    public UltraItemCustomArmor(ArmorMaterial material, int renderIndex, ArmorType armorType, List<String> names) {
        super(material, renderIndex, armorType.getValue());
        
        // Validate that we have the right data coming thru
        assert names.size() == ArmorType.validLength() : "[UltraItemCustomArmor] Invalid number of names passed into constructor!";
        
        // set the custom names as passed into the constructor
        for (int i = 0; i < ArmorType.validLength(); ++i) {
            NAMES[i] = names.get(i);
        }
        
        // setup name and tab
        setUnlocalizedName(NAMES[armorType.getValue()]);
        setCreativeTab(UltraRegistry.ultraCreativeTab);
        
        // register the item
        HubbyUtils.registerNamedItem(UltraMod.MOD_ID, this);
    }

    /**
     * Returns the name for this particular piece of armor based
     * on the current <code>ArmorType</code>
     * @return String - the item name
     */
    @Override
    public String getName() {
        return NAMES[this.armorType];
    }
}
