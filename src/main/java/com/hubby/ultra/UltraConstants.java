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
        NONE     (-1, 3, ""),
        SMALL    (0,  3, "Small"),
        MEDIUM   (1,  3, "Medium"),
        LARGE    (2,  3, "Large");
        
        /**
         * Members
         */
        private Integer _underlyingValue;
        private Integer _inventorySlot;
        private String _nameSuffix;
        
        /**
         * Constructor for enum
         * @param value
         * @param slot
         */
        BackpackType(Integer value, Integer slot, String suffix) {
            _underlyingValue = value;
            _inventorySlot = slot;
            _nameSuffix = suffix;
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
         * Returns the inventory slot that corresponds to the current
         * <code>ArmorType</code> enumerated value
         * @return Integer - the inventory slot
         */
        public Integer getInventorySlot() {
            return _inventorySlot;
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
