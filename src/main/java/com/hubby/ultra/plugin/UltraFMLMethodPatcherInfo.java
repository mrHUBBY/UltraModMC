package com.hubby.ultra.plugin;

/**
 * Simple storage class for the info we need to replace a method
 * @author davidleistiko
 */
public class UltraFMLMethodPatcherInfo {
    
    public String _className;
    public String _superClassName;
    public String _methodName;
    public String _methodDesc;
    public UltraFMLMethodPatcherCallable _patchMethod;
    
    /**
     * Constructor
     * @param className - the class name 
     * @param superClassName - the super class name
     * @param methodName - the method to patch
     * @param methodDesc - the method desc to patch
     * @param patchMethod - the function to patch the method
     */
    UltraFMLMethodPatcherInfo(String className, String superClassName, String methodName, String methodDesc, UltraFMLMethodPatcherCallable patchMethod) {
        _className = className;
        _superClassName = superClassName;
        _methodName = methodName;
        _methodDesc = methodDesc;
        _patchMethod = patchMethod;
    }
    
    /**
     * Do we have a match?
     * @param method - the name of the method
     * @param methodDesc - the desc of the method
     * @return boolean - do we match the params?
     */
    public boolean matches(String method, String methodDesc) {
        return _methodName.equals(method) && _methodDesc.equals(methodDesc);
    }
}
