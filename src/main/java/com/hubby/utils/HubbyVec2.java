package com.hubby.utils;

/**
 * Simple class that represents a 2d vector
 * @author davidleistiko
 * @param <T>
 */
public class HubbyVec2 {
    
    /**
     * Constant vectors that are typically used a lot
     */
    public static final HubbyVec2 ZERO = new HubbyVec2(0.0, 0.0);
    public static final HubbyVec2 ONE = new HubbyVec2(1.0, 1.0);
    public static final HubbyVec2 X = new HubbyVec2(1.0, 0.0);
    public static final HubbyVec2 Y = new HubbyVec2(0.0, 1.0);

    /**
     * the x position for the vector
     */
    protected Double _x;

    /**
     * the y position for the vector
     */
    protected Double _y;

    /**
     * Rotate the vector by the angle with the specified radius
     * @param theta - the angle to rotate by (in radians)
     * @param radius - the radius for rotation
     */
    public static HubbyVec2 vectorWithRotation(Double theta, Double radius) {
        Double x = radius * Math.cos(theta);
        Double y = radius * Math.sin(theta);
        return new HubbyVec2(x, y);
    }

    /**
     * Interpolates the two vectors by the percent specified
     * @param begin - the starting vector
     * @param end - the ending vector to interpolate to
     * @param percent - the percent of the interpolation (must be [0, 1])
     * @return HubbyVec2 - the interpolated vector
     */
    public static HubbyVec2 vectorWithInterpolation(HubbyVec2 begin, HubbyVec2 end, Double percent) {
        assert(percent >= 0.0 && percent <= 1.0) : "[HubbyVec2] Invalid percent specified for interpolation!";
        Double x = (begin._x * (1.0 - percent)) + (end._x * percent);
        Double y = (begin._y * (1.0 - percent)) + (end._y * percent);
        return new HubbyVec2(x, y);
    }

    /**
     * Returns the vector resulting from the addition operation
     * @param one - the first vector to add
     * @param two - the second vector to add to the first
     * @return HubbyVec2 - the resulting vector
     */
    public static HubbyVec2 vectorWithAddition(HubbyVec2 one, HubbyVec2 two) {
        return new HubbyVec2(one._x + two._x, one._y + two._y);
    }

    /**
     * Returns the vector resulting from the subtraction operation
     * @param one - the first vector to subtract
     * @param two - the second vector to subtract from the first
     * @return HubbyVec2 - the resulting vector
     */
    public static HubbyVec2 vectorWithSubtraction(HubbyVec2 one, HubbyVec2 two) {
        return new HubbyVec2(one._x - two._x, one._y - two._y);
    }

    /**
     * Default constructor
     */
    public HubbyVec2() {
        _x = _y = 0.0;
    }

    /**
     * Parameterized constructor
     * @param x - the x position
     * @param y - the y position
     */
    public HubbyVec2(Double x, Double y) {
        _x = x;
        _y = y;
    }
    
    /**
     * Copy constructor
     * @param other - the other vec to assign
     */
    public HubbyVec2(HubbyVec2 other) {
        _x = other._x;
        _y = other._y;
    }

    /**
     * Returns if this vector is normalized
     * @return boolean - are we normalized
     */
    public boolean isNormalized() {
        return HubbyMath.almostEqual(length2(), 1.00);
    }
    
    /**
     * Returns the distance between the two vectors
     * @param other - the other vec to find the distance between
     * @return Double - the distance between the two vectors
     */
    public Double distance(HubbyVec2 other) {
        return Math.sqrt(distance2(other));
    }
    
    /**
     * Returns the squared distance between the two vectors
     * @param other - the other vec to find the distance between
     * @return Double - the squared distance between the two vectors
     */
    public Double distance2(HubbyVec2 other) {
        return ((_x - other._x) * (_x - other._x)) + ((_y * other._y) * (_y - other._y));
    }

    /**
     * Sets the x and y positions respectively
     * @param x - the x position to set
     * @param y - the y position to set
     */
    public void set(Double x, Double y) {
        _x = x;
        _y = y;
    }

    /**
     * Sets the x value
     * @param x - the x to set
     */
    public void setX(Double x) {
        _x = x;
    }

    /**
     * Sets the y value
     * @param y - the y to set
     */
    public void setY(Double y) {
        _y = y;
    }

    /**
     * Returns the x value
     * @return T - the x value
     */
    public Double getX() {
        return _x;
    }

    /**
     * Returns the y value
     * @return T - the value
     */
    public Double getY() {
        return _y;
    }

    /**
     * Offsets the vector to a new location
     * @param x - the amount in the x direction to move
     * @param y - the amount in the y direction to move
     */
    public void offset(Double x, Double y) {
        _x += x;
        _y += y;
    }

    /**
     * Returns the length for this vector
     * @return
     */
    public Double length() {
        return Math.sqrt(length2());
    }

    /**
     * Returns the squared length for this vector
     * @return
     */
    public Double length2() {
        return (_x * _x) + (_y * _y);
    }

    /**
     * Normalizes the vector such that it has a
     * length value equal to one
     */
    public void normalize() {
        Double l = length();
        _x /= l;
        _y /= l;
    }

    /**
     * Adds the other vec to ourselves
     * @param other - the vector to add
     */
    public void add(HubbyVec2 other) {
        _x += other._x;
        _y += other._y;
    }

    /**
     * Subtracts the other vec from ourselves
     * @param other - the other vector
     */
    public void subtract(HubbyVec2 other) {
        _x -= other._x;
        _y -= other._y;
    }

    /**
     * Scales the vector by the specified amount
     * @param scale - the scale to apply
     */
    public void scale(Double scale) {
        _x *= scale;
        _y *= scale;
    }

    /**
     * Returns the dot product with the other vec
     * @param other - the other vector
     * @return Double - the dot product value
     */
    public Double dot(HubbyVec2 other) {
        return _x * other._x + _y * other._y;
    }

    /**
     * Returns the angle in radians between the two vectors
     * @param other - the other vector
     * @return Double - the angle between the vectors (in degrees)
     */
    public Double angle(HubbyVec2 other) {
        return Math.atan2(_y, _x) - Math.atan2(other._y, other._x);
    }

    /**
    * Returns a new instance of a size equal to ourselves
    * @return HubbySize - the new size object
    */
    @Override
    public Object clone() {
        return new HubbyVec2(_x, _y);
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

        HubbyVec2 otherSize = (HubbyVec2) other;
        return _x == otherSize._x && _y == otherSize._y;
    }
}
