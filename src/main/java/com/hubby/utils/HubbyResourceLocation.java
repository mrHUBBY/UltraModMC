package com.hubby.utils;

import net.minecraft.util.ResourceLocation;

public class HubbyResourceLocation extends ResourceLocation {

    /**
     * This member stores additional information about the resource location,
     * such as type distinction to help differentiate between two items that
     * use the same resource but might have a different color
     */
    private Object _metadata;
    
    /**
     * Does this resource location point to a built-in type?
     */
    private boolean _isBuiltin;
    
    /**
     * Constructor
     * @param extra - unused
     * @param resourcePathIn - the array of strings
     */
    protected HubbyResourceLocation(int extra, String[] resourcePathIn) {
        super(extra, resourcePathIn);
    }
    
    /**
     * Constructor
     * @param domainAndPath - this string contains the domain and path in one string
     */
    public HubbyResourceLocation(String domainAndPath) {
        super(0, func_177516_a(domainAndPath));
    }

    /**
     * Constructor
     * @param domain - the domain of the resource
     * @param path - the path of the resource
     */
    public HubbyResourceLocation(String domain, String path) {
        super(0, new String[] {domain, path});
    }
    
    /**
     * Constructor
     * @param domain - the domain of the resource
     * @param path - the path of the resource
     * @param meta - the metadata
     */
    public HubbyResourceLocation(String domain, String path, Object meta) {
        super(0, new String[] {domain, path});
        setMetadata(meta);
    }
    
    /**
     * Constructor using regular <code>ResourceLocation</code>
     * @param rl - the initial resource to copy
     */
    public HubbyResourceLocation(ResourceLocation rl) {
        super(rl.getResourceDomain(), rl.getResourcePath());
    }
    
    /**
     * Copy constructor
     * @param rl - the initial resource to copy
     */
    public HubbyResourceLocation(HubbyResourceLocation rl) {
        super(rl.getResourceDomain(), rl.getResourcePath());
    }

    /**
     * Sets the metadata for the resource
     * @param meta
     */
    public void setMetadata(Object meta) {
        _metadata = meta;
    }
    
    /**
     * Returns the metadata as a specific type
     * @return T - the metadata
     */
    public <T extends Object> T getMetadata(Class<T> klass) { 
        if (klass == null || klass.isInstance(_metadata)) {
            return (T)_metadata;
        }
        return null;
    }
    
    /**
     * Returns if the metadata is of the type passed in
     * @param klass - the class type to check the metadata for
     * @return boolean - are we the type specified?
     */
    public <T extends Object> boolean isMetadataOfType(Class<T> klass) {
        return klass == null || klass.isInstance(_metadata);
    }
    
    /**
     * Checks to see if we are an item
     * @return boolean - are we an item
     */
    public boolean isItemModel() {
        return getResourcePath().contains("models/item/");
    }
    
    /**
     * Checks to see if we are a block
     * @return boolean - are we a block
     */
    public boolean isBlockModel() {
        return getResourcePath().contains("models/block/");
    }
    
    /**
     * Checks if we are any kind of model
     * @return boolean - are we a model?
     */
    public boolean isModel() {
        return getResourcePath().contains("models/");
    }
    
    /**
     * Returns the name of the model if it is a model
     * @return String - the model name (or empty string when invalid)
     */
    public String getModelPath() {
       if (isModel()) {
           Integer lastIndex = getResourcePath().lastIndexOf("/");
           return getResourcePath().substring(0, lastIndex + 1);
       }
       return "";
    }
    
    /**
     * Returns the base name of the model
     * @param boolean - should we include the resource extension if it has one
     * @return String - the base model name (or empty string when invalid)
     */
    public String getModelName(boolean withExtension) {
        if (isModel()) {
            Integer lastIndex = getResourcePath().lastIndexOf("/");
            String model = getResourcePath().substring(lastIndex + 1);
            if (withExtension) {
                return model;
            }
            return model.substring(0, model.indexOf("."));
        }
        return "";
    }
    
    /**
     * Test to see if we are a texture resource
     * @return boolean - are we a texture?
     */
    public boolean isTexture() {
        return getResourcePath().contains(".png") || getResourcePath().contains("textures/");
    }
    
    /**
     * Are we a json resource
     * @return boolean - are we a json?
     */
    public boolean isJson() {
        return getResourcePath().contains(".json");
    }
    
    /**
     * Differentiates between a vanilla minecraft resource and a mod
     * resource by checking the domain
     * @return boolean - are we a vanilla resource?
     */
    public boolean isVanillaResource() {
        return getResourceDomain().equals(HubbyConstants.MINECRAFT_MOD_ID) || 
            (getResourceDomain().length() == 0 && getResourcePath().length() > 0);
    }
}
