package com.hubby.shared.utils;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

/**
 * Defines the interface for a callback that responds
 * to properties being read from the config file
 * @author davidleistiko
 *
 */
public interface HubbyConfigurationPropertyListenerInterface {
	/**
	 * Callback that gets invoked when the config file is being iterated
	 * and a category/property is found
	 * @param prop - the config property
	 * @param category - the category name
	 * @param key - the property name
	 * @param value - the value for the property
	 * @param comment - the comment for the category
	 */
	public void onConfigPropertyRead(Property prop, String category, String key, String value, Type type, String comment);

	/**
	 * Returns all of the category names expected to be found in the config
	 * @param config - the config file
	 * @return boolean - did we create any categories?
	 */
	public boolean createDefaultCategories(Configuration config);
	
	/**
	 * Should create all of the properties that belong to the category named
	 * @param config - the config file
	 * @param category - the name of the category
	 * @return boolean - did we create any properties for the category
	 */
	public boolean createDefaultPropertiesForCategory(Configuration config, String category);
	
	/**
	 * This method should respond to the event of the configuration being cleared and
	 * having all categories removed
	 * @param config - the configuration file
	 */
	public void onConfigurationReset(Configuration config);
}
