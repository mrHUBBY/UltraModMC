package com.hubby.shared.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Callable;

import net.minecraft.client.settings.KeyBinding;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * This class is responsible for loading the configurations
 * from disk that help to determine the behavior of the mod
 * @author davidleistiko
 */
public class ConfigHelper {
		
	/**
	 * The list of registered listeners that will be notified
	 * when a config property is encountered
	 */
	private ArrayList<IConfigPropertyListener> _configListeners = new ArrayList<IConfigPropertyListener>();
	
	/**
	 * The instance of the config helper used for the singleton pattern
	 */
	private static ConfigHelper _instance = null;
	
	/**
	 * The instance of the actual configuration file
	 */
	private Configuration _config = null;
	
	/**
	 * The string that divides the config data into separate values
	 */
	public static final String DELIMITER = ",";

	/**
	 * This static method implements the singleton pattern and exposes
	 * access to the internal configuration file that the user can use
	 * to load mod info.
	 * @return Configuration - the configuration instance
	 */
	public static final ConfigHelper getInstance() {
		if (_instance == null) {
			_instance = new ConfigHelper();
		}
		return _instance;
	}
	
	/**
	 * Default constructor that takes an instance of a File
	 * object as its parameter, and we assume that this file
	 * points to a valid minecraft mod configuration file
	 * @param src
	 */
	protected ConfigHelper() {
		_config = null;
	}
	
	/**
	 * Registers a config property listener
	 * @param listener - the listener to add
	 */
	public void addPropertyListener(IConfigPropertyListener listener) {
		_configListeners.add(listener);
	}
	
	/**
	 * Unregisters a config listener
	 * @param listener - the listener to remove
	 */
	public void removePropertyListener(IConfigPropertyListener listener) {
		_configListeners.remove(listener);
	}
	
	/**
	 * Opens the configuration using the file that is passed in. This
	 * function will thrown an exception if a configuration file is
	 * attempted to be opened while another configuration is already opened
	 * @param file - the source file for the configuration
	 * @return Configuration - the loaded config file
	 * @throws Exception 
	 */
	public Configuration openConfiguration(File file, String version) {
		try {
			_config = new Configuration(file, version, false);
			_config.load();
			
			// parse the contents
			parseConfigurationFile();
		}
		catch (Exception e) {
			System.out.format(
					"Failed to open configuration file [%s] withException [%s]\n", 
					file.getName(), e.getMessage());
			
			_config = null;
		}
		
		// return the new config
		return _config;
	}
	
	/**
	 * Parses the configuration file and invokes all listeners so
	 * that they can respond to the loaded properties
	 */
	protected void parseConfigurationFile() {

		// iterate over all categories and keys to read in all
		// configuration properties, passing these values along
		// to any registered listeners
		Set<String> categories = _config.getCategoryNames();
		for (String cat : categories) {
			ConfigCategory category = _config.getCategory(cat);
			Set<String> keys = category.keySet();
			for (String key : keys) {
				Property prop = category.get(key);
				String value = prop.getString();
				String comment = prop.comment;
				notifyListeners(prop, cat, key, value, comment);
			}
		}
		
		// finally, we save the configuration file so that any default
		// values that were written to the config will now be saved as
		// part of the config, ready for future use
		_config.save();
	}
	
	/**
	 * Attempts to read the property that is identified by the category
	 * and key values passed in
	 * @param category - the category for the value
	 * @param key - the key for the value
	 * @param defaultValue - the default value for the config item
	 * @param comment - the comment to set on the config item
	 * @return Property - the config value
	 */
	public Property getProperty(String category, String key, String comment) {
		// Passing in null for the defaultValue prevents the configuration
		// from adding a new property if it currently does not exist.
		// If you want to add missing properties then call 'addProperty' instead
		Property prop = _config.get(category, key, (String)null, comment);
		notifyListeners(prop, category, key, prop.getString(), comment);
		_config.save();
		return prop;
	}
	
	/**
	 * Adds the specified property with the attributes given
	 * @param category - the property category
	 * @param key - the key for the category
	 * @param defaultValue - the default value for the property
	 * @param comment - the comment that describes the property
	 * @return
	 */
	public Property addProperty(String category, String key, String defaultValue, String comment) {
		Property prop = _config.get(category, key, defaultValue, comment);
		notifyListeners(prop, category, key, prop.getString(), comment);
		_config.save();
		return prop;
	}
	
	/**
	 * Helper method that sets the default value for the
	 * property specified by category and key
	 * @param category
	 * @param key
	 * @param defaultValue
	 * @return boolean - was the setting successful?
	 */
	public boolean setPropertyDefaultValue(String category, String key, String defaultValue) {
		Property prop = _config.get(category, key, defaultValue);
		if (prop == null) {
			return false;
		}
		prop.setDefaultValue(defaultValue);
		_config.save();
		return prop != null;
	}
	
	/**
	 * Sets the current value for the identified property
	 * @param category - the category for the property
	 * @param key - the key for the property
	 * @param constraints - the allowed values
	 * @return boolean - was the setting successful?
	 */
	public boolean setPropertyValue(String category, String key, String value) {
		Property prop = _config.get(category, key, value);
		if (prop == null) {
			return false;
		}
		prop.setValue(value);
		_config.save();
		return prop != null;
	}
	
	/**
	 * Sets the allowed values for the identified property
	 * @param category - the category for the property
	 * @param key - the key for the property
	 * @param constraints - the allowed values
	 * @return boolean - was the setting successful?
	 */
	public boolean setPropertyConstraints(String category, String key, String[] constraints) {
		Property prop = _config.get(category, key, Constants.EMPTY_STRING);
		if (prop == null) {
			return false;
		}
		prop.setValidValues(constraints);
		_config.save();
		return prop != null;
	}
	
	/**
	 * Sets the comment for the identified property
	 * @param category - the category for the property
	 * @param key - the key for the property
	 * @param comment - the comment to set
	 * @return boolean - was the setting successful?
	 */
	public boolean setPropertyComment(String category, String key, String comment) {
		Property prop = _config.get(category, key, Constants.EMPTY_STRING);
		if (prop == null) {
			return false;
		}
		prop.comment = comment;
		_config.save();
		return prop != null;
	}
	
	/**
	 * Helper routine that notifies all listeners about the property
	 * passed in
	 * @param prop - the property we are examining
	 * @param category - the category name
	 * @param key - the string name
	 * @param value - the value for the prop
	 * @param comment - the comment
	 */
	private final void notifyListeners(Property prop, String category, String key, String value, String comment) {
		for (IConfigPropertyListener listener : _configListeners) {
			listener.onConfigPropertyRead(prop, category, key, value, comment);
		}
	}
}
