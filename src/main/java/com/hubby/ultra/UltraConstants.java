package com.hubby.ultra;

/**
 * This class simply holds a bunch of constants
 * @author davidleistiko
 */
public class UltraConstants {

    /**
     * These values are used for the UltraItemAdvancedArmor
     */
    public static final int ULTRA_ITEM_ADVANCED_ARMOR_RENDER_INDEX = 3;
    public static final int ULTRA_ITEM_ADVANCED_ARMOR_DURABILITY = 50;
    public static final int ULTRA_ITEM_ADVANCED_ARMOR_ENCHANTABILITY = 25;
    public static final int[] ULTRA_ITEM_ADVANCED_ARMOR_DAMAGE_REDUCTIONS = { 8, 12, 10, 8 };
    
    /**
     * These values are used for the UltraItemBackpackArmor
     */
    public static final int ULTRA_ITEM_BACKPACK_ARMOR_RENDER_INDEX = 3;
    public static final int ULTRA_ITEM_BACKPACK_ARMOR_DURABILITY = 40;
    public static final int ULTRA_ITEM_BACKPACK_ARMOR_ENCHANTABILITY = 10;
    public static final int[] ULTRA_ITEM_BACKPACK_ARMOR_DAMAGE_REDUCTIONS = { 4, 6, 5, 4 };
    
    /**
     * This enumerates the various types of available backpacks
     * @author davidleistiko
     */
    public enum BackpackType {
        NONE     (-1, 0,  0,  "",         "", ""),
        SMALL    (0,  27, 24, "Small",    "container.ultraGuiContainerBackpackSmall", "ultraBackpackSmallInventory.dat"),
        MEDIUM   (1,  35, 48, "Medium",   "container.ultraGuiContainerBackpackMedium", "ultraBackpackMediumInventory.dat"),
        LARGE    (2,  43, 64, "Large",    "container.ultraGuiContainerBackpackLarge", "ultraBackpackLargeInventory.dat");
        
        /**
         * Members
         */
        private Integer _underlyingValue;
        private String _nameSuffix;
        private Integer _inventorySize;
        private Integer _inventoryStackSizeLimit;
        private String _inventoryFilename;
        private String _containerName;
        
        /**
         * Constructor for enum
         * @param value
         * @param slot
         */
        BackpackType(Integer value, Integer inventorySize, Integer maxInventoryStackSize, String suffix, String containerName, String inventoryFilename) {
            _underlyingValue = value;
            _inventorySize = inventorySize;
            _nameSuffix = suffix;
            _inventoryFilename = inventoryFilename;
            _inventoryStackSizeLimit = maxInventoryStackSize;
            _containerName = containerName;
        }
        
        /**
         * Returns the numeric representation for the current
         * <code>ArmorType</code> that is being evaluated
         * @return
         */
        public Integer getValue() {
            return _underlyingValue;
        }
        
        /**
         * Returns the container name for the backpack type
         * @return String - the container name
         */
        public String getContainerName() {
            return _containerName;
        }
        
        /**
         * Returns the max amount a stack can be for the backpack type
         * @return Integer - the max inventory stack size
         */
        public Integer getInventoryStackSizeLimit() {
            return _inventoryStackSizeLimit;
        }
        
        /**
         * Returns the inventory filename for the backpack size
         * @return String - the filename where we save the inventory
         */
        public String getInventoryFilename() {
            return _inventoryFilename;
        }
        
        /**
         * Returns the inventory size for the backpack type
         * @return Integer - the inventory size
         */
        public Integer getInventorySize() {
            return _inventorySize;
        }
        
        /**
         * Returns the suffix that is used to build the item name
         * for the backpack
         * @return
         */
        public String getSuffix() {
            return _nameSuffix;
        }
        
        /**
         * Returns the valid enum count
         * @return
         */
        public static Integer validLength() {
            return 3;
        }
    }
}
