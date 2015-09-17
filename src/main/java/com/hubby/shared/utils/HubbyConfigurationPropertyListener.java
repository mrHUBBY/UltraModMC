package com.hubby.shared.utils;

import java.util.ArrayList;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

/**
 * Represents an event that is fired when a config value
 * is encountered during the reading of the config file
 * @author davidleistiko
 */
public class HubbyConfigurationPropertyListener implements HubbyConfigurationPropertyListenerInterface {
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
		System.out.println("Configuration Property [" + category + "][" + key + "]");
		System.out.println("\tcomment: " + comment);
		System.out.println("\tvalue: " + value);
		System.out.println("\ttype: " + type.toString());
	}
	
	/**
	 * Return the default categories
	 * @param config - the config file
	 * @return boolean - this will always be false here
	 */
	@Override
	public boolean createDefaultCategories(Configuration config) {
		return false;
	}

	/**
	 * Return the default properties for the category
	 * @param config - the config file
	 * @param category - the category to get properties for
	 * @return boolean - this will always be false here
	 */
	@Override
	public boolean createDefaultPropertiesForCategory(Configuration config, String category) {
		return false;
	}
}
