package com.hubby.shared.utils;

import net.minecraftforge.common.config.Property;

/**
 * Defines the interface for a callback that responds
 * to properties being read from the config file
 * @author davidleistiko
 *
 */
public interface IConfigPropertyListener {
	public void onConfigPropertyRead(Property prop, String category, String key, String value, String comment);
}
