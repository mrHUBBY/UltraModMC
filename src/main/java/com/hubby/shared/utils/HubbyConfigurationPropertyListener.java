package com.hubby.shared.utils;

import net.minecraftforge.common.config.Property;

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
	 */
	@Override
	public void onConfigPropertyRead(Property prop, String category, String key, String value, String comment) {
		System.out.println("Configuration Property [" + category + "][" + key + "]");
		System.out.println("\tcomment: " + comment);
		System.out.println("\tvalue: " + value);
	}
}
