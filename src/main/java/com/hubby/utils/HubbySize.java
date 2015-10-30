package com.hubby.utils;

/**
 * This class stores a width and a height as a templated class
 * meant to be used with Integer, Double, Float, Long etc.
 * @author davidleistiko
 * @param <T>
 */
public class HubbySize<T extends Number> {

    /**
     * Members
     */
    protected T _width;
    protected T _height;
    
    /**
     * Default constructor
     */
    public HubbySize() {
        _width = _height = (T)(Integer)0;
    }
    
    /**
     * Parameterized constructor, sets width and height
     * @param width - the width to set
     * @param height - the height to set
     */
    public HubbySize(T width, T height) {
        _width = width;
        _height = height;
    }
    
    /**
     * Copy constructor
     * @param other - the other vec to assign
     */
    public HubbySize(HubbySize<T> other) {
        _width = other._width;
        _height = other._height;
    }
    
    /**
     * Sets the width and the height
     * @param w - the width to set
     * @param h - the height to set
     */
    public void set(T w, T h) {
        _width = w;
        _height = h;
    }
    
    /**
     * Access to the width
     * @return T - the width
     */
    public T getWidth() {
        return _width;
    }
    
    /**
     * Access to the height
     * @return T - the height
     */
    public T getHeight() {
        return _height;
    }
    
    /**
     * Sets the desired width
     * @param width - the width to set
     */
    public void setWidth(T width) {
        _width = width;
    }
    
    /**
     * Sets the desired height
     * @param height - the height to set
     */
    public void setHeight(T height) {
        _height = height;
    }
    
    /**
     * Returns a new instance of a size equal to ourselves
     * @return HubbySize - the new size object
     */
    @Override
    public Object clone() {
        return new HubbySize<T>(_width, _height);
    }
    
    /**
     * Overrides the equality operator for checking if two sizes are
     * equal or not
     * @param other - the other object to compare
     * @return boolean - are we to be considered equal?
     */
    @Override
    public boolean equals(Object other) {
        if (other.getClass() != HubbySize.class) {
            return false;
        }
        
        HubbySize<T> otherSize = (HubbySize<T>)other;
        return _width == otherSize._width && _height == otherSize._height;
    }
}
