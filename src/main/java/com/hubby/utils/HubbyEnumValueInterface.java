package com.hubby.utils;

/**
 * Simple interface to be used with enums that
 * additionally need to be represented by a unique
 * integer value other than what ordinal() returns
 * @author davidleistiko
 *
 */
public interface HubbyEnumValueInterface {
    /**
     * Should return an integer unique from all other
     * enums that are contained within
     * @return Integer - the value for the enum
     */
    Integer getValue();
}
