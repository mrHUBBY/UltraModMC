package com.hubby.ultra.items;

import com.hubby.shared.utils.INamedObject;
import com.hubby.shared.utils.Utils;
import com.hubby.ultra.setup.UltraMod;
import com.hubby.ultra.setup.UltraRegistry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * A custom implementation of a ItemSword
 * @author davidleistiko
 */
public class UltraItemBasicSword extends ItemSword implements INamedObject {

	/**
	 * The name of this item, used for identifying resource files
	 */
	public static final String NAME = "ultraItemBasicSword";
	
	/**
	 * How much damage this sword takes when it strikes an entity
	 */
	public static final int DEFAULT_DAMAGE_PER_HIT = 1; 
	
	/**
	 * Constructor
	 * @param material - the material that defines the stats for the sword
	 */
	public UltraItemBasicSword(ToolMaterial material) {
		super(material);
		this.setUnlocalizedName(NAME);
		this.setCreativeTab(UltraRegistry.ultraCreativeTab);
		
		// actually register the item with forge and mc for rendering
		Utils.registerNamedItem(UltraMod.MOD_ID, this);
	}
	
	/**
	 * Implement INamedObject, return object name
	 */
	public String getName() {
		return NAME;
	}
	
	/**
	 * Returns the damage against the entity passed in
	 * @param par1Entity
	 * @return
	 */
	@Override
	public float getDamageVsEntity() {
		return super.getDamageVsEntity();
	}
	
	/**
	 * Callback that is fired when an entity is about to be struck by
	 * this particular sword
	 */
	// Handle when we hit an ent with our sword
    @Override
    public boolean hitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase attacker) {
    	itemStack.damageItem(DEFAULT_DAMAGE_PER_HIT, attacker);
    	// TODO:
    	// Add custom feature that causes entities hit with this sword
    	// to take on a potion effect and drop a particular item
    	// see commented out code below
    	// par2EntityLivingBase.addPotionEffect(new PotionEffect(NitroInterface.nitroPotion.id, NitroInterface.nitroPotionDurationTicks, 1, false));
    	// par2EntityLivingBase.entityDropItem(new ItemStack(NitroInterface.nitroOre, 2), 5.0f);
        return true;
    }
    
    /**
     * Returns an array of creative tabs that we should appear on
     * @return CreativeTabs[] - the array of valid tabs
     */
    @Override
    public CreativeTabs[] getCreativeTabs() {
		return new CreativeTabs[]{CreativeTabs.tabCombat, UltraRegistry.ultraCreativeTab};
    }
}

