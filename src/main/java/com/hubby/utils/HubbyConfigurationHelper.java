package com.hubby.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

/**
 * This class is responsible for loading the configurations
 * from disk that help to determine the behavior of the mod
 * @author davidleistiko
 */
public class HubbyConfigurationHelper {
		
	/**
	 * The list of registered listeners that will be notified
	 * when a config property is encountered
	 */
	private ArrayList<HubbyConfigurationPropertyListenerInterface> _configListeners = new ArrayList<HubbyConfigurationPropertyListenerInterface>();
	
	/**
	 * The instance of the config helper used for the singleton pattern
	 */
	private static HubbyConfigurationHelper _instance = null;
	
	/**
	 * The instance of the actual configuration file
	 */
	private Configuration _config = null;
	
	/**
	 * Should we reset the config file when we open it for parsing?
	 */
	private boolean _resetConfigurationFile = false;
	
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
	public static final HubbyConfigurationHelper getInstance() {
		if (_instance == null) {
			_instance = new HubbyConfigurationHelper();
		}
		return _instance;
	}
	
	/**
	 * Default constructor that takes an instance of a File
	 * object as its parameter, and we assume that this file
	 * points to a valid minecraft mod configuration file
	 * @param src
	 */
	protected HubbyConfigurationHelper() {
		_config = null;
		_resetConfigurationFile = false;
	}
	
	/**
	 * Sets whether or not to reset the config file to its default
	 * values when it is opened and parsed
	 * @param reset - should we reset?
	 */
	public void setResetConfigurationFile(boolean reset) {
	    _resetConfigurationFile = reset;
	}
	
	/**
	 * Registers a config property listener
	 * @param listener - the listener to add
	 */
	public void addPropertyListener(HubbyConfigurationPropertyListenerInterface listener) {
		_configListeners.add(listener);
	}
	
	/**
	 * Unregisters a config listener
	 * @param listener - the listener to remove
	 */
	public void removePropertyListener(HubbyConfigurationPropertyListenerInterface listener) {
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
			
			// reset the config if we are told to
			if (_resetConfigurationFile) {
			    clearConfigurationFile();
			    _resetConfigurationFile = false;
			}
			
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
		
		attemptToGenerateDefaultConfigurationFile();

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
				notifyListeners(prop, cat, key, value, prop.getType(), comment);
			}
		}
		
		// If we do not have any categories then we assume that we have a config
		// that has not had its default values set yet. Sending this message to
		// the listeners with a null Property indicates that the listeners should
		// apply their default values
		if (categories.size() == 0) {
			notifyListeners(null, "", "", "", null, "");
		}
		
		// finally, we save the configuration file so that any default
		// values that were written to the config will now be saved as
		// part of the config, ready for future use
		_config.save();
	}
	
	/**
	 * This function tries to collect information about all of the categories
	 * and properties for those categories. In the case that the config file
	 * is empty, this class will call all of the config listeners, expecting them
	 * to add their default values if they have any
	 */
	protected void attemptToGenerateDefaultConfigurationFile() {
		getCategories();
		Set<String> categories = _config.getCategoryNames();
		for (String cat : categories) {
			getPropertiesForCategory(cat);
		}
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
		notifyListeners(prop, category, key, prop.getString(), prop.getType(), comment);
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
		notifyListeners(prop, category, key, prop.getString(), prop.getType(), comment);
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
		Property prop = _config.get(category, key, HubbyConstants.EMPTY_STRING);
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
		Property prop = _config.get(category, key, HubbyConstants.EMPTY_STRING);
		if (prop == null) {
			return false;
		}
		prop.comment = comment;
		_config.save();
		return prop != null;
	}
	
	/**
	 * Returns a list of all category names
	 * @return ArrayList - the category names
	 */
	public ArrayList<String> getCategories() {
		Set<String> categories = _config.getCategoryNames();
		if (categories.size() == 0) {
			boolean created = false;
			for (HubbyConfigurationPropertyListenerInterface listener : _configListeners) {
				created |= listener.createDefaultCategories(_config);
			}
			return created ? getCategories() : new ArrayList<String>();
		}
		return new ArrayList<String>(categories);
	}
	
	/**
	 * Clears the configuration file by removing all categories
	 */
	public void clearConfigurationFile() {
	    Set<String> categories = _config.getCategoryNames();
	    Iterator<String> it = categories.iterator();
	    while (it.hasNext()) {
	        String categoryName = it.next();
	        _config.removeCategory(_config.getCategory(categoryName));
	    }
	    
	    // notify listeners
	    for (HubbyConfigurationPropertyListenerInterface listener : _configListeners) {
            listener.onConfigurationReset(_config);
        }
	}
	
	/**
	 * Returns a list of property names for the category passed in. In the
	 * case that the category does not exist an empty list is returned
	 * @param category - the name of the category
	 * @return ArrayList - the list of properties for the specified category
	 */
	public ArrayList<String> getPropertiesForCategory(String category) {
		ArrayList<String> cats = getCategories();
		if (cats.contains(category)) {
			ConfigCategory configCategory = _config.getCategory(category);
			Set<String> keys = configCategory.keySet();
			if (keys.size() == 0) {
				boolean created = false;
				for (HubbyConfigurationPropertyListenerInterface listener : _configListeners) {
					created |= listener.createDefaultPropertiesForCategory(_config, category);
				}
				return created ? getPropertiesForCategory(category) : new ArrayList<String>();
			}
			return new ArrayList<String>(configCategory.keySet());
		}
		return new ArrayList<String>();
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
	private final void notifyListeners(Property prop, String category, String key, String value, Type type, String comment) {
		// nothing to do without a valid property
		if (prop == null) {
			return;
		}
		
		// notify all listeners about the property that was just parsed
		for (HubbyConfigurationPropertyListenerInterface listener : _configListeners) {
			listener.onConfigPropertyRead(prop, category, key, value, type, comment);
		}
	}
}
