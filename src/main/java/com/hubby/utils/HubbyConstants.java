package com.hubby.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HubbyConstants {
    /**
     * Useful constants
     */
	public static final String EMPTY_STRING = "";
    public static final Double TICKS_PER_SECOND = 20.0D;
    public static final Double SECONDS_PER_TICK = 1.0D / TICKS_PER_SECOND;
    public static final Integer TARGTE_FRAME_RATE = 30;
    public static final Integer HOTBAR_INVENTORY_SIZE = 9;
    public static final Integer HOTBAR_INVENTORY_OFFSET = 36;
    
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
    
    /**
     * The various types of priority allowed for logging
     * @author davidleistiko
     *
     */
    public enum LogChannel {
        INFO        ("{INFO}", true),
        DEBUG       ("{DEBUG}", true),
        WARNING     ("{WARNING}", true),
        ERROR       ("{ERROR}", true);
        
        /**
         * Members
         */
        private String _tag = "";
        private boolean _enabled = false;
        private List<String> _appendLines = new ArrayList<String>();
        private boolean _isAppendMode = false;
        private static boolean _logToConsole = true;
        private static final HashMap<LogChannel, ArrayList<String>> LOGS = new HashMap<LogChannel, ArrayList<String>>();
        private static final String SEPARATOR = " >> ";
        
        /**
         * Static constructor
         */
        static {
            LOGS.put(LogChannel.INFO, new ArrayList<String>());
            LOGS.put(LogChannel.DEBUG, new ArrayList<String>());
            LOGS.put(LogChannel.WARNING, new ArrayList<String>());
            LOGS.put(LogChannel.ERROR, new ArrayList<String>());
        }
        
        /**
         * Returns the class name without the package prefix
         * @param klass - the class to get the name for
         * @return String - the returned name
         */
        public static String getClassName(Class klass) {
            String name = klass.getName();
            Integer packageNameEndIndex = name.lastIndexOf(".");
            return "{" + name.substring(packageNameEndIndex + 1) + "}";
        }
        
        /**
         * Enum constructor
         * @param tag
         */
        LogChannel(String tag, boolean enabled) {
            _tag = tag;
            _enabled = enabled;
        }
        
        /**
         * Returns the tag
         * @return
         */
        public String getLogTag() {
            return _tag;
        }
        
        /**
         * Returns whether or not the channel is enabled
         * @return
         */
        public boolean isEnabled() {
            return _enabled;
        }
        
        /**
         * Sets the channel enabled or not
         * @param enabled - enable the channel or not
         */
        public void setEnabled(boolean enabled) {
            _enabled = enabled;
        }
        
        /**
         * Log including the class name of the one logging
         * @param klass - the class sending the log message
         * @param format - the format
         * @param args - the formatting args
         */
        public void log(Class klass, String format, Object... args) {
            format = LogChannel.getClassName(klass) + SEPARATOR + format;
            log(format, args);
        }
        
        /**
         * Starts an appended log message which allows for multiple lines to
         * be chained together so that the user does not have to worry about new lines
         * @param klass - the class to log
         * @param format - the format of the log
         * @param args - the args for the log message
         * @return LogChannel - returns this so that commands can be linked together
         */
        public LogChannel start(Class klass, String format, Object... args) {
            format = LogChannel.getClassName(klass) + SEPARATOR + format;
            return start(format, args);
        }
        
        /**
         * Starts an appended log message which allows for multiple lines to
         * be chained together so that the user does not have to worry about new lines
         * @param format - the format of the log
         * @param args - the args for the log message
         * @return LogChannel - returns this so that commands can be linked together
         */
        public LogChannel start(String format, Object... args) {
            assert !_isAppendMode : "Attempting to start an appended log message while already in append mode; call end() first!";
            _appendLines.clear();
            _isAppendMode = true;
            return append(format, args);
        }
        
        /**
         * Appends a new line to be logged once <code>end()</code> is called
         * @param format - the format of the string
         * @param args - the args for formatting the string
         * @return LogChannel - returns this for chaining appends together
         */
        public LogChannel append(String format, Object... args) {
            assert _isAppendMode : "Attempting to append a log message when not in append mode; call start() first";
            _appendLines.add(((args == null) ? format : String.format(format, args)));
            return this;
        }
        
        /**
         * Closes the append mode
         */
        public void end() {
            assert _isAppendMode : "Attempting to close an appended log message when not in append mode; call start() first";
            String finalMessage = "";
            for (String s : _appendLines) {
                finalMessage += s;
                if (_appendLines.indexOf(s) < _appendLines.size() - 1) {
                    finalMessage += "\n";
                }
            }
            _isAppendMode = false;
            log(finalMessage, (Object)null);
        }
        
        /**
         * Log message
         * @param format - the message format
         * @param args - the formatting args
         */
        public void log(String format, Object... args) {
            
            assert !_isAppendMode : "Attempting to log a standard message while still in append mode; call end() first!";
        
            // don't log if we are not enabled...
            if (!isEnabled()) {
                return;
            }
            
            // Prepend the log tag if not done so yet
            if (!format.startsWith(getLogTag())) {
                format = getLogTag() + SEPARATOR + format;
            }
            
            // add the log message to the log cache
            LOGS.get(this).add(((args == null) ? format : String.format(format, args)));
            
            // print to console optionally
            if (LogChannel._logToConsole) {
                int lastIndex = LOGS.get(this).size() - 1;
                System.out.println(LOGS.get(this).get(lastIndex));
            }
        }
        
        /**
         * Enables/disabled the logging to the console
         * @param enabled - are we enabled?
         */
        public static void enableConsoleLogging(boolean enabled) {
            LogChannel._logToConsole = enabled;
        }
        
        /**
         * Static helper function for checking channel
         * @param channel - the channel to check
         * @return boolean - is the channel enabled
         */
        public static boolean isChannelEnabled(LogChannel channel) {
            return channel.isEnabled(); 
        }
        
        /**
         * Logs the message
         * @param channel - the channel to log to
         * @param format - the format
         * @param args - the arguments for formatting
         */
        public static void log(LogChannel channel, String format, Object... args) {
            channel.log(format, args);
        }
    }
    
    /**
     * Helper to identify click types for gui screens with
     * containers/inventories that can be interacted with
     * @author davidleistiko
     */
    public enum ClickType {
        
        INVALID         (-1),
        BASIC_CLICK     (0),
        SHIFT_CLICK     (1),
        HOTBAR          (2),
        PICK_BLOCK      (3),
        DROP            (4),
        UNKNOWN         (5),
        DOUBLE_CLICK    (6);
        
        /**
         * Members
         */
        private Integer _underlyingValue;
        
        /**
         * Returns a ClickType that has a matching underlying value
         * @param value - the value to match
         * @return ClickType - the corresponding click type
         */
        public static ClickType getEnumForValue(Integer value) {
            for (ClickType type : ClickType.values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return ClickType.INVALID;
        }
        
        /**
         * Constructor
         * @param underlyingValue
         */
        ClickType(Integer underlyingValue) {
            _underlyingValue = underlyingValue;
        }
        
        /**
         * Get the underlying value
         * @return Integer - the value for the enum
         */
        public Integer getValue() {
            return _underlyingValue;
        }
    }
    
    /**
     * Enumerates the mouse buttons that the user can click
     * @author davidleistiko
     */
    public enum ClickButton {
        
        INVALID             (-1),
        LEFT_BUTTON         (0),
        RIGHT_BUTTON        (1),
        
        HOTBAR_SLOT1        (2),
        HOTBAR_SLOT2        (3),
        HOTBAR_SLOT3        (4),
        HOTBAR_SLOT4        (5),
        HOTBAR_SLOT5        (6),
        HOTBAR_SLOT6        (7),
        HOTBAR_SLOT7        (8),
        HOTBAR_SLOT8        (9),
        HOTBAR_SLOT9        (10);
        
        /**
         * Members
         */
        private Integer _underlyingValue;
        
        /**
         * Finds the corresponding enum with a matching underlying value
         * @param value - the value to match
         * @return ClickButton - the matching enum value
         */
        public static ClickButton getEnumForValue(Integer value) {
            for (ClickButton button : ClickButton.values()) {
                // Here we do not want to call button.getValue()
                // as that will adjust the returned value if the button
                // is a hotbar type, so instead, we just access the
                // value directly
                if (button._underlyingValue == value) {
                    return button;
                }
            }
            return ClickButton.INVALID;
        }
        
        /**
         * Test enum to see if it is a hotbar type
         * @param button
         * @return
         */
        public static boolean isHotbar(ClickButton button) {
            return button._underlyingValue >= ClickButton.HOTBAR_SLOT1._underlyingValue && 
                   button._underlyingValue <= ClickButton.HOTBAR_SLOT9._underlyingValue;
        }
        
        /**
         * Returns the value adjustment for the hotbar
         * @return
         */
        public static Integer getHotbarValueOffset() {
            return 2;
        }
        
        /**
         * Constructor
         */
        ClickButton(Integer value) {
            _underlyingValue = value;
        }
        
        /**
         * Returns the underlying value for the enum
         * @return
         */
        public Integer getValue() {
            if (_underlyingValue >= ClickButton.HOTBAR_SLOT1._underlyingValue) {
                return _underlyingValue - getHotbarValueOffset();
            }
            return _underlyingValue;
        }
        
        /**
         * Test if this value is a hotbar type
         * @return
         */
        public boolean isHotbar() {
            return ClickButton.isHotbar(this);
        }
    }

    /**
     * Enum that corresponds to the type of drag event that the user
     * can perform with the mouse
     * @author davidleistiko
     */
    public enum DragEvent {
        
        INVALID     (-1),
        START       (0),
        ADD_SLOT    (1),
        END         (2);
        
        /**
         * Members
         */
        private Integer _underlyingValue;
        
        /**
         * Gets the DragEvent enum with the matching underlying value
         * @param value - the value to match
         * @return DragEvent - the matching enum
         */
        public static DragEvent getEnumForValue(Integer value) {
            for (DragEvent drag : DragEvent.values()) {
                if (drag._underlyingValue == value) {
                    return drag;
                }
            }
            return DragEvent.INVALID;
        }
        
        /**
         * Constructor
         * @param value
         */
        DragEvent(Integer value) {
            _underlyingValue = value;
        }
        
        /**
         * Returns the underlying value
         * @return Integer - the value
         */
        public Integer getValue() {
            return _underlyingValue;
        }
    }
    
    /**
     * Defines the single INVALID packet type that can be used by both
     * the client and server and anyone wanting to use packet types enum
     * @author davidleistiko
     */
    public enum HubbyGenericPacketType implements HubbyEnumValueInterface {
        INVALID                     (-1);
     
        /**
         * Members
         */
        private Integer _underlyingValue;
       
        /**
         * Constructor
         * @param displayName - the display name
         */
        HubbyGenericPacketType(Integer value) {
            _underlyingValue = value;
        }
        
        /**
         * Returns the packet id
         * @return Integer - the packet id
         */
        @Override
        public Integer getValue() {
            return _underlyingValue;
        }
    }
    
    /**
     * The various packets that we send from the client to the server
     */
    public enum HubbyClientPacketType implements HubbyEnumValueInterface {
        PLAYER_INVENTORY           (99);        

        /**
         * Members
         */
        private Integer _underlyingValue;
        
        /**
         * Constructor
         * @param displayName - the display name
         */
        HubbyClientPacketType(Integer value) {
            _underlyingValue = value;
        }
        
        /**
         * Returns the packet id
         * @return Integer - the packet id
         */
        @Override
        public Integer getValue() {
            return _underlyingValue;
        }
    }
}
