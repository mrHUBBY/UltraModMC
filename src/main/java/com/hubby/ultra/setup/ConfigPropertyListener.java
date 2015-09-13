package com.hubby.ultra.setup;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.hubby.shared.utils.ConfigHelper;
import com.hubby.shared.utils.IConfigPropertyListener;
import com.hubby.shared.utils.StringEx;

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
		
		ConfigHelper.getInstance().setPropertyDefaultValue(CATEGORY_INPUT, KEY_KEYBINDINGS, 
				StringEx.str(/*
				key.rain=19,key.lights=38,
				key.time=44,key.options=24,
				key.effects=33,key.backpack=48,
				key.cheat=46,key.teleport=45,
				key.everything=47,key.super=25,
				key.nightGoggles=34
				*/));
		
		ArrayList<KeyBinding> list = new ArrayList<KeyBinding>();
		String[] keys = value.split(ConfigHelper.DELIMITER);
		for (String binding : keys) {
			String[] nameAndKey = binding.split(KEYBINDING_DELIMITER);
			list.add(new KeyBinding(nameAndKey[0], Integer.parseInt(nameAndKey[1]), bindingKeyCategory));
		}
		return list;
	}
}