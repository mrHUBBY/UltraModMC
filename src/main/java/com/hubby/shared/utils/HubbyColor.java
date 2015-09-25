package com.hubby.shared.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.lwjgl.opengl.GL11;

/**
 * This class provides a number of color converting functions
 * to give more control to the user
 * @author davidleistiko
 */
public class HubbyColor {

	/**
	 * Constants
	 */
	public static final HubbyColor WHITE = new HubbyColor(1.0f, 1.0f, 1.0f, 1.0f);
	public static final HubbyColor RED = new HubbyColor(1.0f, 0.0f, 0.0f, 1.0f);
	public static final HubbyColor BLUE = new HubbyColor(0.0f, 0.0f, 1.0f, 1.0f);
	public static final HubbyColor GREEN = new HubbyColor(0.0f, 1.0f, 0.0f, 1.0f);
	public static final HubbyColor SKY_BLUE = new HubbyColor(0x3FA2FFFFFFL, ColorMode.STANDARD);
	public static final HubbyColor LIGHT_PURPLE = new HubbyColor(0xAE7FFFFFL, ColorMode.STANDARD);
	public static final HubbyColor BROWN = new HubbyColor(0x491B0FFFL, ColorMode.STANDARD);
	public static final HubbyColor YELLOW = new HubbyColor(0xFFED4CFFL, ColorMode.STANDARD);
	public static final HubbyColor PINK = new HubbyColor(0xFF9BACFFL, ColorMode.STANDARD);
	public static final HubbyColor LIGHT_GREEN = new HubbyColor(0x9BFFA0FFL, ColorMode.STANDARD);
	public static final HubbyColor CYAN = new HubbyColor(0x00FFFFFFL, ColorMode.STANDARD);
	public static final HubbyColor MAGENTA = new HubbyColor(0xFF00FFFFL, ColorMode.STANDARD);
	public static final HubbyColor BLACK = new HubbyColor(0x000000FFL, ColorMode.STANDARD);
	public static final HubbyColor GREY = new HubbyColor(0x888888FFL, ColorMode.STANDARD);
	public static final HubbyColor ORANGE = new HubbyColor(0xFF6000FFL, ColorMode.STANDARD);
	
	private static final Map<String, HubbyColor> _colorLookupByName = new HashMap<String, HubbyColor>();

	/**
	 * Members
	 */
	protected float _red = 1.0f;
	protected float _green = 1.0f;
	protected float _blue = 1.0f;
	protected float _alpha = 1.0f;

	/**
	 * Enumeration for color-modes
	 * @author davidleistiko
	 *
	 */
	public enum ColorMode {
		STANDARD,
		MINECRAFT
	}
	
	/**
	 * Builds a collection of common colors that can be looked
	 * up by name
	 */
	public static void buildColorCollection() {
	    if (_colorLookupByName.size() == 0) {
    	    _colorLookupByName.put("red", RED);
    	    _colorLookupByName.put("blue", BLUE);
    	    _colorLookupByName.put("green", GREEN);
    	    _colorLookupByName.put("yellow", YELLOW);
    	    _colorLookupByName.put("cyan", CYAN);
    	    _colorLookupByName.put("magenta", MAGENTA);
    	    _colorLookupByName.put("skyblue", SKY_BLUE);
    	    _colorLookupByName.put("lightpurple", LIGHT_PURPLE);
    	    _colorLookupByName.put("brown", BROWN);
    	    _colorLookupByName.put("pink", PINK);
    	    _colorLookupByName.put("lightgreen", LIGHT_GREEN);
    	    _colorLookupByName.put("white", WHITE);
    	    _colorLookupByName.put("black", BLACK);
    	    _colorLookupByName.put("grey", GREY);
    	    _colorLookupByName.put("orange", ORANGE);
	    }
	}
	
	/**
	 * Parses the string value and generates a HubbyColor based on the
	 * contents of the string
	 * @param colorStr
	 * @return
	 */
	public static HubbyColor parseColor(String colorStr) {

	    // Check if we are a hex color
	    boolean isHexColor = false;
        if (colorStr.startsWith("0x")) {
            colorStr = colorStr.substring(2);
            isHexColor = true;
        }
        
        // if we are a hex-color then parse the string to get the components
        if (isHexColor && colorStr.length() == 8) {
            float red = Integer.parseInt((String) colorStr.subSequence(0, 2), 16) / 255.0f;
            float green = Integer.parseInt((String) colorStr.subSequence(2, 4), 16) / 255.0f;
            float blue = Integer.parseInt((String) colorStr.subSequence(4, 6), 16) / 255.0f;
            float alpha = Integer.parseInt((String) colorStr.subSequence(6, 8), 16) / 255.0f;
            return new HubbyColor(red, green, blue, alpha);
        }
        // if we have no digits, then we assume that we are a valid
        // string value that can be used to lookup an existing color
        else if (!colorStr.matches(".*\\d.*")) {
            return HubbyColor.getColorFromString(colorStr);
        }
        
        // If we get here then we assume that we are a integer value
        // and we parse that to generate the packed color value
        int colorValue = Integer.parseInt(colorStr);
        return new HubbyColor((long)colorValue, ColorMode.STANDARD);
	}
	
    /**
     * Returns a pseudo-randomly generated color value with a specific alpha
     * @param alpha - the value for the alpha channel
     * @return Color - the random color
     */
    public static HubbyColor getRandomColor(float alpha) {
        final Random random = new Random(System.currentTimeMillis());
        float r = ((random.nextInt() % 256) / 255.0f);
        float g = ((random.nextInt() % 256) / 255.0f);
        float b = ((random.nextInt() % 256) / 255.0f);
        return new HubbyColor(r, g, b, alpha);
    }
    
    /**
     * Converts the regular color value to a minecraft color
     * @param color - the color to convert
     * @return Integer - the color value
     */
    public static Integer convertToMinecraftColor(Integer color) {
        float r = (float)((color >> 24) & 255) / 255.0f;
        float g = (float)((color >> 16) & 255) / 255.0f;
        float b = (float)((color >> 8) & 255) / 255.0f;
        float a = (float)((color >> 0) & 255) / 255.0f;
        
        Integer mcColor = 0;
        mcColor |= ((int)(a * 255)) << 24;
        mcColor |= ((int)(r * 255)) << 16;
        mcColor |= ((int)(g * 255)) << 8;
        mcColor |= ((int)(b * 255)) << 0;
        return mcColor;
    }

    /**
     * Converts the minecraft color to a regular color format
     * @param color - the color to convert
     * @return Integer - the color value
     */
    public static Integer convertToStandardColor(Integer color) {
        float r = (float)((color >> 16) & 255) / 255.0f;
        float g = (float)((color >> 8) & 255) / 255.0f;
        float b = (float)((color >> 0) & 255) / 255.0f;
        float a = (float)((color >> 24) & 255) / 255.0f;
        
        Integer standardColor = 0;
        standardColor |= ((int)(r * 255)) << 24;
        standardColor |= ((int)(g * 255)) << 16;
        standardColor |= ((int)(b * 255)) << 8;
        standardColor |= ((int)(a * 255)) << 0;
        return standardColor;
    }
    
    /**
     * Attempts to fecth a color from the collection by name,
     * if the name is not registered then a random color is added
     * @param colorStr
     * @return
     */
    public static HubbyColor getColorFromString(String colorStr) {
        HubbyColor color = _colorLookupByName.get(colorStr);
        return color == null ? WHITE : color;
    }
    
    /**
     * Adds a custom color to the collection which can be used later
     * @param colorName - the name of the color
     * @param color - the color to store
     */
    public static void addColorToCollection(String colorName, HubbyColor color) {
        _colorLookupByName.put(colorName, color);
    }

	/**
	 * Constructor that takes in specific values for each color channel
	 * @param r - the red value
	 * @param g - the green value
	 * @param b - the blue value
	 * @param a - the alpha value
	 */
	public HubbyColor(float r, float g, float b, float a) {
		setChannels(r, g, b, a);
	}

	/**
	 * Constructor that uses a packed value containing all color info
	 * @param value - the packed color value
	 * @param isMinecraftColor - should we treat this in a special way
	 */
	public HubbyColor(long value, ColorMode mode) {
		unpackColor(value, mode);
	}
	
	/**
	 * Converts the color from whatever it's current mode is to
	 * the mode passed into this function
	 * @param mode - the color mode to convert to
	 */
	public void convertTo(ColorMode mode) {
	    ColorMode sourceMode = mode == ColorMode.STANDARD ? ColorMode.MINECRAFT : ColorMode.STANDARD;
	    unpackColor(getPackedColor(sourceMode), mode);
	}
	
	/**
	 * Sets the color channels to the provided values
	 * @param r - the red component
	 * @param g - the green component
	 * @param b - the blue component
	 * @param a - the alpha component
	 */
	public void setChannels(float r, float g, float b, float a) {
		_red = HubbyMath.clamp(r, 0.0f, 1.0f);
		_green = HubbyMath.clamp(g, 0.0f, 1.0f);
		_blue = HubbyMath.clamp(b, 0.0f, 1.0f);
		_alpha = HubbyMath.clamp(a, 0.0f, 1.0f);
	}
	
	/**
	 * Sets the red channel
	 * @param r - the red value
	 */
	public void setRed(float r) {
		_red = HubbyMath.clamp(r, 0.0f, 1.0f);
	}
	
	/**
	 * Sets the green channel
	 * @param g - the green value
	 */
	public void setGreen(float g) {
		_green = HubbyMath.clamp(g, 0.0f, 1.0f);
	}
	
	/**
	 * Sets the blue channel
	 * @param b - the blue value
	 */
	public void setBlue(float b) {
		_blue = HubbyMath.clamp(b, 0.0f, 1.0f);
	}
	
	/**
	 * Sets the alpha channel
	 * @param a - the alpha value
	 */
	public void setAlpha(float a) {
		_alpha = HubbyMath.clamp(a, 0.0f, 1.0f);
	}
	
	/**
	 * Retrieves the red channel
	 * @return float - the red value
	 */
	public float getRed() {
		return _red;
	}
	
	/**
	 * Retrieves the green channel
	 * @return float - the green value
	 */
	public float getGreen() {
		return _green;
	}
	
	/**
	 * Retrieves the blue channel
	 * @return float - the blue value
	 */
	public float getBlue() {
		return _blue;
	}
	
	/**
	 * Retrieves the alpha channel
	 * @return float - the alpha value
	 */
	public float getAlpha() {
		return _alpha;
	}
	
	/**
	 * Changes the color from full color to black and white
	 */
	public void convertToGrayScale() {
	    float modValue = (_red * 0.299f) + (_green * 0.587f) + (_blue * 0.114f);
	    setChannels(modValue, modValue, modValue, _alpha);
	}
	
	/**
	 * Converts the color from full color to sepia tones
	 */
	public void convertToSepia() {
		convertToGrayScale();
		float r = HubbyMath.clamp(_red * 1.2f, 0.0f, 1.0f);
		float g = HubbyMath.clamp(_green * 1.0f, 0.0f, 1.0f);
		float b = HubbyMath.clamp(_blue * 0.8f, 0.0f, 1.0f);
		float a = HubbyMath.clamp(_alpha, 0.0f, 1.0f);
		setChannels(r, g, b, a);
	}
	
	/**
	 * Gradually move towards gray-scale coloring with a ratio
	 * of zero being full-color and a ration of one being totally
	 * black and white
	 * @param ratio - how much should we desaturate?
	 */
	public void desaturate(float ratio) {
		ratio = HubbyMath.clamp(ratio, 0.0f, 1.0f);
		float coefficient = (_red * 0.299f) + (_green * 0.587f) + (_blue * 0.114f);
		float r = (coefficient * ratio) + (_red * (1.0f - ratio));
		float g = (coefficient * ratio) + (_green * (1.0f - ratio));
		float b = (coefficient * ratio) + (_blue * (1.0f - ratio));
		setChannels(r, g, b, _alpha);
	}

	/**
	 * Takes all color channels and compacts them into a single value
	 * @return - the value of the packed color
	 */
	public long getPackedColor(ColorMode mode) {
		long color = 0;
		if (mode == ColorMode.STANDARD) {
			color |= (long)(_red * 255.0F) << 24L;
			color |= (long)(_green * 255.0F) << 16L;
			color |= (long)(_blue * 255.0F) << 8L;
			color |= (long)(_alpha * 255.0F);
		}
		else if (mode == ColorMode.MINECRAFT) {
			color |= (long)(_alpha * 255.0f) << 24L;
			color |= (long)(_red * 255.0F) << 16L;
			color |= (long)(_green * 255.0F) << 8L;
			color |= (long)(_blue * 255.0F) << 0L;
		}
		return color;
	}

	/**
	 * Unpacks the color based on the color mode and
	 * then assigns the values to the members
	 * @param color - the color to unpack
	 * @param mode - the mode that determines how we unpack
	 */
	public void unpackColor(long color, ColorMode mode) {
		if (mode == ColorMode.MINECRAFT) {
			_red = (float)((color >> 16) & 255) / 255.0f;
			_green = (float)((color >> 8) & 255) / 255.0f;
			_blue = (float)((color >> 0) & 255) / 255.0f;
			_alpha = (float)((color >> 24) & 255) / 255.0f;
		}
		else if (mode == ColorMode.STANDARD) {
			_red = (float)((color >> 24) & 255) / 255.0f;
			_green = (float)((color >> 16) & 255) / 255.0f;
			_blue = (float)((color >> 8) & 255) / 255.0f;
			_alpha = (float)((color >> 0) & 255) / 255.0f;
		}
	}

	/**
	 * This helper method let's us take our current color
	 * values and then send those off to the openGL world
	 * so that the next color operation will use our color
	 */
	public void applyColorGL() {
		GL11.glColor4f(_red, _green, _blue, _alpha);
	}
}
