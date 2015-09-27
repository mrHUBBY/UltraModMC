package com.hubby.ultra.setup;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.hubby.shared.utils.HubbyConfigurationHelper;
import com.hubby.shared.utils.HubbyConfigurationPropertyListenerInterface;
import com.hubby.shared.utils.HubbyUtils;

import net.minecraft.client.settings.KeyBinding;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

/**
 * Represents an event that is fired when a config value
 * is encountered during the reading of the config file
 * @author davidleistiko
 */
public class UltraConfigPropertyListener implements HubbyConfigurationPropertyListenerInterface {
	
	/**
	 * Constants
	 */
	public static final String CATEGORY_INPUT = "input";
	public static final String KEY_KEYBINDINGS = "keys";
	public static final String KEYBINDING_DELIMITER = "=";
	public static final String ALTERNATE_DELIMITER =":";
    public static final String KEY_BINDING_OPEN_TELEPORT_GUI = "openTeleportGuiKey";
	public static final String KEY_BINDING_OPEN_BACKPACK_GUI = "openBackpackGuiKey";

	/**
	 * Members
	 */
	protected final List<KeyBinding> _keyBindings = new ArrayList<KeyBinding>();
	
	/**
	 * Gives access to the loaded key-bindings, and makes it so
	 * the caller cannot modify this list at all
	 * @return ImmutableList - the list of key bindings
	 */
	public final ImmutableList<KeyBinding> getKeyBindings() {
		return ImmutableList.copyOf(_keyBindings);
	}
	
	/**
	 * Implementation for the callback listener method. Simply logs the property
	 * to the console and then returns. Others, can implement their own callback
	 * to properly respond to the property.
	 * @param prop - the parsed property
	 * @param category - the owning category
	 * @param key - the property name
	 * @param value - the property value
	 * @param type - the property type
	 * @param comment - the property comment
	 */
	@Override
	public void onConfigPropertyRead(Property prop, String category, String key, String value, Type type, String comment) {
		if (category.matches(CATEGORY_INPUT) && key.matches(KEY_KEYBINDINGS)) {
			_keyBindings.clear();
			_keyBindings.addAll(parseKeyBindings(value, "key.categories." + UltraMod.MOD_NAME));
		}
	}
	
	/**
	 * Helper methods for parsing out the key bindings
	 * @param value - the value of all key-bindings needing to be parsed
	 * @param bindingKeyCategory - the input category name for the keys
	 * @return List - the list of parsed key-bindings
	 */
	private List<KeyBinding> parseKeyBindings(String value, String bindingKeyCategory) {
		
		// iterate over the list of key bindings by splitting the value
		// using the appropriate delimiter keys
		ArrayList<KeyBinding> list = new ArrayList<KeyBinding>();
		String[] keys = value.split(HubbyConfigurationHelper.DELIMITER);
		for (String binding : keys) {
			String[] nameAndKey = binding.split(KEYBINDING_DELIMITER);
			String[] keyAndAlias = nameAndKey[1].split(ALTERNATE_DELIMITER);
			Integer keyID = Integer.parseInt(keyAndAlias[0]);
			String keyAlias = keyAndAlias.length > 1 ? keyAndAlias[1] : keyID.toString();
			KeyBinding keyBinding = new KeyBinding(nameAndKey[0], Integer.parseInt(keyAndAlias[0]), bindingKeyCategory);
			list.add(keyBinding);
			
			HubbyUtils.regiterKeyBinding(keyAlias, keyBinding);
		}
		return list;
	}

	/**
	 * This method will be called when an empty config file is being parsed.
	 * We can use this moment to create all of the categories we would
	 * expect to find in the config normally
	 * @param config - the config file
	 * @return boolean - did we create a category?
	 */
	@Override
	public boolean createDefaultCategories(Configuration config) {
		config.getCategory(CATEGORY_INPUT);
		return true;
	}

	/**
	 * This method will be called in the event that an empty category
	 * was encountered while parsing the config file. We can use this
	 * moment to create all of the properties we would normally expect
	 * to find on the named category.
	 * @param config - the config file
	 * @param category - the current category
	 * @return boolean - did we create any properties?
	 */
	@Override
	public boolean createDefaultPropertiesForCategory(Configuration config, String category) {
		if (category.contains(CATEGORY_INPUT)) {
			config.get(category, KEY_KEYBINDINGS, 
					"key.rain=19:toggleRainKey," +
					"key.lights=38:togleLightsKey," +
					"key.time=44:toggleTimeKey," +
					"key.options=24:openOptionsKey," +
					"key.effects=33:toggleEffectsKey," +
					"key.backpack=48:openBackpackGuiKey," +
					"key.cheat=46:giveCheatsKey," +
					"key.teleport=45:openTeleportGuiKey," +
					"key.everything=47:openEverythingInventoryKey," +
					"key.super=25:openSuperInventoryKey," +
					"key.nightGoggles=34:toggleNightGogglesKey", 
					"These are the key definitions for this mod");
			return true;
		}
		return false;
	}

	/**
	 * Called when the config file is cleared
	 * @param config - the cleared config
	 */
    @Override
    public void onConfigurationReset(Configuration config) { 
    }
}