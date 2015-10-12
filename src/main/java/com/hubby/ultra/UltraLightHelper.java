package com.hubby.ultra;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.base.Predicate;
import com.hubby.utils.HubbyConstants.LightLevel;
import com.hubby.utils.HubbyRefreshedObjectInterface;
import com.hubby.utils.HubbyUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * This class helps to manage all of the dynamic light objects and
 * entities as they move through the world, properly applying their
 * light level to the objects and world nearby
 * @author davidleistiko
 */
public class UltraLightHelper extends HubbyRefreshedObjectInterface {

    /**
     * Members
     */
    private boolean _lightItemsEnabled = true;
    private boolean _lightNodesEnabled = true;
    private IBlockAccess _lastWorld;
    private ConcurrentLinkedQueue<UltraLightSourceNode> _lastLightsList;
    private ConcurrentHashMap<World, ConcurrentLinkedQueue<UltraLightSourceNode>> _worldLightsMap;
    
    /**
     * Constructor
     */
    public UltraLightHelper(String id, int priority) {
        super(id, priority);
        
        _worldLightsMap = new ConcurrentHashMap<World, ConcurrentLinkedQueue<UltraLightSourceNode>>();
    }

    /**
     * the singleton instance
     */
    private static final UltraLightHelper INSTANCE = new UltraLightHelper("refreshLightHelper", HubbyRefreshedObjectInterface.HIGHEST_PRIORITY);
    
    /**
     * Returns the instance of the light helper
     * @return UltraLightHelper - the instance
     */
    public static UltraLightHelper getInstance() {
        return INSTANCE;
    }
    
    /**
     * Toggles the node lights on and off
     */
    public void toggleNodeLights() {
        _lightNodesEnabled = !_lightNodesEnabled;
    }
    
    /**
     * Toggles the item lights on and off
     */
    public void toggleItemLights() {
        _lightItemsEnabled = !_lightItemsEnabled;   
    }
    
    /**
     * Enables or disables the node lights
     * @param enable - should we enable?
     */
    public void enableNodeLights(boolean enable) {
        _lightNodesEnabled = enable;
    }
    
    /**
     * Enables or disables the item lights
     * @param enable - should we enable?
     */
    public void enableItemLights(boolean enable) {
        _lightItemsEnabled = enable;
    }
    
    /**
     * Are the item lights enabled or not
     * @return boolean - enabled?
     */
    public boolean areLightItemsEnabled() {
        return _lightItemsEnabled;
    }
    
    /**
     * Are the node lights enabled or not
     * @return boolean - enabled?
     */
    public boolean areLightNodesEnabled() {
        return _lightNodesEnabled;
    }
    
    /**
     * Retrieve a list of all items that implement the light interface
     * @return List - the list of all light items currently registered
     */
    public static List<Item> collectLightItems() {
      
        final Predicate<Item> predicate = new Predicate<Item>() {
            @Override
            public boolean apply(Item item) {
                return UltraLightItemInterface.class.isInstance(item);
               //return HubbyUtils.getInventoryItemLocations(item.getClass()).size() > 0;
            }
        };
        return HubbyUtils.collectAllItems(predicate);
    }
    
    /**
     * This is the method that will override the world's method 
     * <code>getRawLight</code> for calculating the
     * correct light value for a block
     * @param block - the block to check
     * @param world - the current world
     * @param pos - the position in the world
     * @return int - the light value
     */
    public static int getLightValue(Block block, IBlockAccess world, BlockPos pos) {
        
        // retrieve the vanilla value and use that if lights are currently disabled
        // or we are not on the client side of things
        int vanillaLightValue = block.getLightValue(world, pos);
        if (!UltraLightHelper.getInstance()._lightNodesEnabled || !HubbyUtils.isClientSide(world)) {
            return vanillaLightValue;
        }

        // Set the world and the current list of lights that we will be working with
        if (!world.equals(UltraLightHelper.getInstance()._lastWorld) || UltraLightHelper.getInstance()._lastLightsList == null) {
            UltraLightHelper.getInstance()._lastWorld = world;
            UltraLightHelper.getInstance()._lastLightsList = ((ConcurrentLinkedQueue)UltraLightHelper.getInstance()._worldLightsMap.get(world));
            if (UltraLightHelper.getInstance()._lastLightsList == null) {
                UltraLightHelper.getInstance()._worldLightsMap.put((World)world, new ConcurrentLinkedQueue<UltraLightSourceNode>());
                UltraLightHelper.getInstance()._lastLightsList = ((ConcurrentLinkedQueue)UltraLightHelper.getInstance()._worldLightsMap.get(world));
            }
        }
        
        // Not much to do if we do not have any lights in our list
        if (UltraLightHelper.getInstance()._lastLightsList == null || UltraLightHelper.getInstance()._lastLightsList.isEmpty()) {
            return vanillaLightValue;
        }

        // determine the override light value by checking for all
        // lights that might be in the same position as the block that
        // we are currently calculating the light value for
        int overrideLightValue = 0;
        for (UltraLightSourceNode light : UltraLightHelper.getInstance()._lastLightsList) {    
            if (light.getPos().equals(pos)) {
                overrideLightValue = Math.max(light.getLightSource().getLightLevel().getValue(), overrideLightValue);
            }
        }
        
        // return the max value
        return Math.max(vanillaLightValue, overrideLightValue);
    }
    
    /**
     * Returns the default light level for the block specified by position
     * @param pos - the pos to get the light level for
     * @return int - the light value (between 0 - 15)
     */
    public int getVanillaLightValueForBlock(BlockPos pos) {
        if (UltraLightHelper.getInstance()._lastWorld != null) {
            World world = (World)UltraLightHelper.getInstance()._lastWorld;
            return world.getBlockState(pos).getBlock().getLightValue(world, pos);
        }
        return LightLevel.getDefaultLightLevel().getValue();
    }
    
    /**
     * Adds a light node to our current light list
     * @param node - the node to add
     * @return boolean - did we add anything?
     */
    public boolean addNodeLight(UltraLightSourceNode node) {
        ConcurrentLinkedQueue<UltraLightSourceNode> lights = _worldLightsMap.get(node.getLightSource().getAttachmentEntity().worldObj);
        if (lights == null) {
            lights = new ConcurrentLinkedQueue<UltraLightSourceNode>();
            _worldLightsMap.put(node.getLightSource().getAttachmentEntity().worldObj, lights);  
        }
        lights.add(node);
        return true;
    }
    
    /**
     * Removes a light node from our registered list
     * @param node - the node to remove
     * @return boolean - did we remove anything?
     */
    public boolean removeNodeLight(UltraLightSourceNode node) {
        ConcurrentLinkedQueue<UltraLightSourceNode> lights = _worldLightsMap.get(node.getLightSource().getAttachmentEntity().worldObj);
        if (lights != null) {
            lights.remove(node);
            return true;
        }
        return false;
    }
    
    /**
     * Returns the light node for the entity passed in
     * @param entity - the entity to look for in our light list
     * @return UltraLightSourceNode - the light node containing the entity (otherwise null)
     */
    public UltraLightSourceNode getLightForEntity(Entity entity) {
        ConcurrentLinkedQueue<UltraLightSourceNode> lights = _worldLightsMap.get(entity.worldObj);
        if (lights != null) {
            Iterator<UltraLightSourceNode> it = lights.iterator();
            while (it.hasNext()) {
                UltraLightSourceNode node = it.next();
                if (node.getLightSource().getAttachmentEntity() == entity) {
                    return node;
                }
            }
        }
        return null;
    }
 
    /**
     * Called every frame, use to update all of the current,
     * active lights
     */
    @Override
    public void refresh(Long delta, Long elapsed) {
        
        // update all items
        if (_lightItemsEnabled) {
            updateLightItems();
        }
        
        // update all nodes
        if (_lightNodesEnabled && _lastLightsList != null) {
            updateLightNodes();
        }
    }
    
    /**
     * Updates all dynamic light items
     */
    protected LightLevel updateLightItems() {
        
//        int finalLightValue = LightLevel.MIN_LIGHT_LEVEL.getValue();
//        
//        List<Item> items = UltraLightHelper.collectLightItems();
//        for (Item item : items) {
//            UltraLightItemInterface lightItem = (UltraLightItemInterface)item;
//            
//            for (int i = 0; i < HubbyConstants.HOTBAR_INVENTORY_SIZE; ++i) {
//                ItemStack itemStack = HubbyUtils.getClientPlayer().inventory.mainInventory[i];
//                Item inventoryItem = itemStack != null ? itemStack.getItem() : null;
//                if (inventoryItem == lightItem) {
//                    int lightValue = lightItem.getLightLevel(itemStack).getValue();
//                    finalLightValue = Math.max(lightValue, finalLightValue);
//                }
//            }
//        }
//        
//        return LightLevel.getEnumForValue(finalLightValue);
        return LightLevel.LEVEL_0;
    }
    
    /**
     * Updates all dynamic light nodes for entities
     * (includes the player as well)
     */
    protected void updateLightNodes() {
        
        // iterate over all light nodes and
        // run their refresh method to update the light pos
        // for those lights which are still valid and then
        // remove any lights that are determined to be inactive
        Iterator<UltraLightSourceNode> it = _lastLightsList.iterator();
        while (it.hasNext()) {
            
            // Get the container and attempt a refresh... if the
            // value returned is false that means the life of the current
            // light is about to end and needs to be removed and cleaned up
            UltraLightSourceNode node = it.next();
            if (!node.refresh()) {
                node.resetLightLevel();
                it.remove();
            }
        }
    }
    
    private ItemStack[] mainInventory;
    private ItemStack[] armorInventory;
    
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        ItemStack[] var3 = this.mainInventory;

        if (index >= var3.length)
        {
            index -= var3.length;
            var3 = this.armorInventory;
        }

        var3[index] = stack;
    }
}
