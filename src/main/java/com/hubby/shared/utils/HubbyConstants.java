package com.hubby.shared.utils;

public class HubbyConstants {
    /**
     * Useful constants
     */
	public static final String EMPTY_STRING = "";
    public static final Double TICKS_PER_SECOND = 20.0D;
    public static final Double SECONDS_PER_TICK = 1.0D / TICKS_PER_SECOND;
    public static final Integer TARGTE_FRAME_RATE = 30;
    
    /**
     * This enum identifies the various pieces of armor as well
     * as their corresponding inventory slot positions. It should be noted,
     * that when we are calling <code>getCurrentArmor</code> we will be
     * using the <code>_inventorySlot</code> value -1, while on the other
     * hand when we are using <code>setCurrentItemOrArmor</code> we would
     * use the value <code>_inventorySlot</code> as is since the 0 position
     * is reserved for the item that the entity is currently using
     * @author davidleistiko
     */
    public enum ArmorType {
        INVALID     (-1, -1),
        HELMET      (0, 4),
        CHESTPLATE  (1, 3),
        LEGGINGS    (2, 2),
        BOOTS       (3, 1);
        
        /**
         * Members
         */
        private Integer _underlyingValue;
        private Integer _inventorySlot;
        
        /**
         * Constructor
         * @param value - the numeric value for the enum
         * @param slot - the inventory slot position for the <code>ArmorType</code>
         */
        ArmorType(Integer value, Integer slot) {
            _underlyingValue = value;
            _inventorySlot = slot;
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
         * Returns the valid enum count
         * @return
         */
        public static Integer validLength() {
            return 4;
        }
    }
    
    /**
     * Enumerates the ordinal directions
     * @author davidleistiko
     */
    public enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }
}
