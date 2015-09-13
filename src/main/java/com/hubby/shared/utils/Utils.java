package com.hubby.shared.utils;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;

/**
 * This class stores a number of static methods that perform
 * useful tasks making life a little bit easier
 * @author davidleistiko
 */
public class Utils {
	
	/**
	 * Simple helper function to get full path for a mod item
	 * @param modId - the name of the mod
	 * @param path - the sub-path to the resource
	 * @return String - the full qualified mod resource name
	 */
	public static final String getResourceLocation(String modId, String path) {
		return modId + ":" + path;
	}
}
