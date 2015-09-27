package com.hubby.ultra.items;

import com.hubby.shared.utils.HubbyNamedObjectInterface;
import com.hubby.shared.utils.HubbyUtils;
import com.hubby.ultra.UltraConstants.BackpackType;
import com.hubby.ultra.gui.UltraGuiScreenBackpack;
import com.hubby.ultra.setup.UltraMod;
import com.hubby.ultra.setup.UltraRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * A custom container that the player can where on their back
 * @author davidleistiko
 */
public class UltraItemBackpack extends Item implements HubbyNamedObjectInterface {

    /**
     * Members
     */
    private static String[] NAMES = {"ultraItemBackpackSmall", "ultraItemBackpackMedium", "ultraItemBackpackLarge" };
    private BackpackType _backpackType;
    
    /**
     * Constructor
     * @param type
     */
    public UltraItemBackpack(BackpackType type) {
        _backpackType = type;
        
        setUnlocalizedName(getName());
        setCreativeTab(UltraRegistry.ultraCreativeTab);
        setMaxStackSize(1);
        
        // actually register the item with forge and mc for rendering
        HubbyUtils.registerNamedItem(UltraMod.MOD_ID, this);
    }
    
    /**
     * Returns the value for this backpack
     * @return BackpackType - the type of backpack
     */
    public BackpackType getBackpackType() {
        return _backpackType;
    }
    
    /**
     * Returns the name for this item
     * @return String - the name of the item
     */
    @Override
    public String getName() {
        return NAMES[_backpackType.getValue()];
    }
    
    /**
     * Returns the texture for the backpack model to be
     * rendered on the back of the player
     * @param type
     * @return
     */
    public static String getModelTexture(BackpackType type) {
        String modifier = type.getSuffix();
        return HubbyUtils.getResourceLocation(UltraMod.MOD_ID, "textures/models/armor/ultra_backpack_armor_" + modifier + "_layer_1.png");
    }
    
    /**
     * Called when the user clicks the right mosue button for this item
     * @param itemStack - the item in the players hand (should be backpack)
     * @param world - the world
     * @parma player - the player
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        super.onItemRightClick(itemStack, world, player);
        if (HubbyUtils.isClienSide()) {
            Minecraft.getMinecraft().displayGuiScreen(new UltraGuiScreenBackpack(HubbyUtils.getClientPlayer()));
        }
        return itemStack;
    }
}
