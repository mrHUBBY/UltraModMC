package com.hubby.shared.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.primitives.Chars;

/**
 * This class stores information about which key strokes
 * should be ignored and which ones should be passed on to
 * their super implementation
 * @author davidleistiko
 */
public class HubbyInputFilter {

	// region - Members
	protected boolean _enabled = true;
	protected Set<Character> _filteredCharList = new HashSet<Character>(32);
	// endregion
	
	/**
	 * Default constructor
	 */
	public HubbyInputFilter() {
	}
	
	/**
	 * Constructor
	 * @param value - the string of chars to add to the filter
	 */
	public HubbyInputFilter(String value) {
		addCharFilter(value);
	}
	
	/**
	 * Constructor
	 * @param value - the list of characters to add to the filter
	 */
	public HubbyInputFilter(List<Character> value) {
		addCharFilter(value);
	}
	
	/**
	 * Constructor
	 * @param value - the single character to add to the filter
	 */
	public HubbyInputFilter(Character value) {
		addCharFilter(value);
	}
	
	/**
	 * Adds a single character to our set of
	 * filtered characters
	 * @param value - the character to add
	 */
	public void addCharFilter(Character value) {
		_filteredCharList.add(value);
	}
	
	/**
	 * Adds all characters in the string to our set
	 * of filtered characters
	 * @param value - the string of characters to add
	 */
	public void addCharFilter(String value) {
		for (char c : value.toCharArray()) {
			addCharFilter(c);
		}
	}
	
	/**
	 * Adds all characters in the list to our set
	 * of filtered characters
	 * @param value - the list of characters to be added
	 */
	public void addCharFilter(List<Character> value) {
		_filteredCharList.addAll(value);
	}
	
	/**
	 * Removes a single character from the filtered list
	 * @param value - the character to remove
	 */
	public void removeCharFilter(Character value) {
		_filteredCharList.remove(value);
	}
	
	/**
	 * Removes all characters in the string from the filtered list
	 * @param value - the character to remove
	 */
	public void removeCharFilter(String value) {
		for (char c : value.toCharArray()) {
			removeCharFilter(c);
		}
	}
	
	/**
	 * Removes all characters in the list from the filtered list
	 * @param value - the list of characters to remove
	 */
	public void removeCharFilter(List<Character> value) {
		_filteredCharList.removeAll(value);
	}
	
	/**
	 * Returns whether or not the char is within
	 * our set of filtered characters.
	 * @param ch - the char to check
	 * @return boolean - was the char in the filtered list?
	 */
	public boolean isFiltered(Character ch) {
		return _filteredCharList.contains(ch);
	}
	
	/**
	 * Returns whether or not the string is within
	 * our set of filtered characters.
	 * @param value - the string of chars to check
	 * @return boolean - was the entire string of chars contained within the filtered list?
	 */
	public boolean isFiltered(String value) {
		return _filteredCharList.containsAll(Chars.asList(value.toCharArray()));
	}
}
