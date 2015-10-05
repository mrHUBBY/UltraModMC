package com.hubby.ultra.plugin;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

/**
 * Class defines the transformer class
 * @author davidleistiko
 */
public class UltraFMLCorePlugin implements IFMLLoadingPlugin {
    
    /**
     * Returns the name of all transformer classes being used
     * @return String[] - list of transforming classes
     */
    public String[] getASMTransformerClass()
    {
        return new String[] { "com.hubby.ultra.plugin.UltraFMLTransformerLights" };
    }

    /**
     * Return the mod container class
     * @return String - the name of the class (can be null)
     */
    public String getModContainerClass()
    {
        return null;
    }

    /**
     * Returns the name of the setup class
     * @return  String - the name of the setup class can be null
     */
    public String getSetupClass()
    {
        return null;
    }

    /**
     * Inject data into the plugin
     * @param data - the data to inject
     */
    public void injectData(Map<String, Object> data)
    {
    }

    /**
     * Return the access transformer class
     * @return String - can be null
     */
    public String getAccessTransformerClass()
    {
        return null;
    }
}