package com.hubby.utils;

public interface HubbyNamedObjectInterface {

	/**
	 * Access directly to the field, can be overriden in any classes
	 * implementing this interface.
	 */
	public static final String MISSING_NAME = "[[missing-name]]";
	
	/**
	 * Should return a string that identifies the object it represents by name
	 * @return
	 */
	public String getName();
}
