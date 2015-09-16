package com.hubby.ultra.setup;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.hubby.shared.utils.ConfigHelper;
import com.hubby.shared.utils.IConfigPropertyListener;
import com.hubby.shared.utils.StringEx;
import com.hubby.shared.utils.Utils;

import net.minecraft.client.settings.KeyBinding;

import net.minecraftforge.common.config.Property;

/**
 * Represents an event that is fired when a config value
 * is encountered during the reading of the config file
 * @author davidleistiko
 */
public class ConfigPropertyListener implements IConfigPropertyListener {
	
	// region - constants
	public static final String CATEGORY_INPUT = "input";
	public static final String KEY_KEYBINDINGS = "keys";
	public static final String KEYBINDING_DELIMITER = "=";
	public static final String ALTERNATE_DELIMITER =":";
	public static final String KEY_BINDING_OPEN_TELEPORT_GUI = "openTeleportGuiKey";
	// endregion
	
	// region - members
	protected final List<KeyBinding> _keyBindings = new ArrayList<KeyBinding>();
	// endregion
	
	/**
	 * Gives access to the loaded key-bindings, and makes it so
	 * the caller cannot modify this list at all
	 * @return
	 */
	public final ImmutableList<KeyBinding> getKeyBindings() {
		return ImmutableList.copyOf(_keyBindings);
	}
	
	/**
	 * Implementation for the callback listener method. Simply logs the property
	 * to the console and then returns. Others, can implement their own callback
	 * to properly respond to the property.
	 */
	@Override
	public void onConfigPropertyRead(Property prop, String category, String key, String value, String comment) {
		
		if (prop == null) {
			applyDefaultValues();
		}
		
		// Handle the key-bindings
		if (category.matches(CATEGORY_INPUT) && key.matches(KEY_KEYBINDINGS)) {
			_keyBindings.clear();
			_keyBindings.addAll(parseKeyBindings(value, "key.categories." + UltraMod.MOD_NAME));
		}
	}
	
	/**
	 * Helper methods for parsing out the key bindings
	 * @param value
	 */
	private List<KeyBinding> parseKeyBindings(String value, String bindingKeyCategory) {
		
		// iterate over the list of key bindings by splitting the value
		// using the appropriate delimiter keys
		ArrayList<KeyBinding> list = new ArrayList<KeyBinding>();
		String[] keys = value.split(ConfigHelper.DELIMITER);
		for (String binding : keys) {
			String[] nameAndKey = binding.split(KEYBINDING_DELIMITER);
			String[] keyAndAlias = nameAndKey[1].split(ALTERNATE_DELIMITER);
			Integer keyID = Integer.parseInt(keyAndAlias[0]);
			String keyAlias = keyAndAlias.length > 1 ? keyAndAlias[1] : keyID.toString();
			KeyBinding keyBinding = new KeyBinding(nameAndKey[0], Integer.parseInt(keyAndAlias[0]), bindingKeyCategory);
			list.add(keyBinding);
			
			Utils.regiterKeyBinding(keyAlias, keyBinding);
		}
		return list;
	}
	
	/**
	 * This function applies the default values to the config file
	 */
	private void applyDefaultValues() {
		
		// Set the default values for the key bindings
		ConfigHelper.getInstance().setPropertyDefaultValue(CATEGORY_INPUT, KEY_KEYBINDINGS,
				"key.rain=19:toggleRainKey," +
				"key.lights=38:togleLightsKey," +
				"key.time=44:toggleTimeKey," +
				"key.options=24:openOptionsKey," +
				"key.effects=33:toggleEffectsKey," +
				"key.backpack=48:openBackpackInvenytoryKey," +
				"key.cheat=46:giveCheatsKey," +
				"key.teleport=45:openTeleportGuiKey," +
				"key.everything=47:openEverythingInventoryKey," +
				"key.super=25:openSuperInventoryKey," +
				"key.nightGoggles=34:toggleNightGogglesKey");
//				StringEx.str(/*
//				key.rain=19:toggleRainKey,
//				key.lights=38:togleLightsKey,
//				key.time=44:toggleTimeKey,
//				key.options=24:openOptionsKey,
//				key.effects=33:toggleEffectsKey,
//				key.backpack=48:openBackpackInvenytoryKey,
//				key.cheat=46:giveCheatsKey,
//				key.teleport=45:openTeleportGuiKey,
//				key.everything=47:openEverythingInventoryKey,
//				key.super=25:openSuperInventoryKey,
//				key.nightGoggles=34:toggleNightGogglesKey
//				*/));
	}
}