package com.hubby.ultra.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.hubby.utils.HubbyConstants.LogChannel;

/**
 * A helper class that functions by patching any methods defined using
 * bytecode to replace the bytes for a standard function with whatever
 * else we want to replace it with
 * @author davidleistiko
 */
public class UltraFMLMethodPatcher extends ClassVisitor implements Opcodes {
    
    /**
     * Stores the list of all methods we will be patching
     */
    private ArrayList<UltraFMLMethodPatcherInfo> _methodInfos = new ArrayList<UltraFMLMethodPatcherInfo>();
    
    
    /**
     * Constructor
     * @param cv - the class visitor
     * @param names - the list of methods to patch
     * @param descs - the list of method args to patch
     * @param fields - the list of fields to patch
     * @param className - the name of the class we are patching
     * @param superName - the super class name
     */
    public UltraFMLMethodPatcher(ClassVisitor cv, List<UltraFMLMethodPatcherInfo> infos) {
        super(Opcodes.ASM4, cv);
        _methodInfos.addAll(infos);
    }
    
    /**
     * Adds a new patch info to our list that we will use to patch
     * methods as we are visited
     * @param info - the info to add
     */
    public void addMethodPatchInfo(UltraFMLMethodPatcherInfo info) {
        UltraFMLMethodPatcherInfo patchInfo = getPatchInfoForMethod(info._methodName, info._methodDesc);
        if (patchInfo != null) {
            Integer index = _methodInfos.indexOf(patchInfo);
            _methodInfos.set(index, info);
        }
        else {
            _methodInfos.add(info);
        }
    }
    
    /**
     * Returns the patch info for the method specified
     * @param method - the method name
     * @param desc - the method desc
     * @return MethodPatcherInfo - the corresponding patch info (null if the method is not found)
     */
    public UltraFMLMethodPatcherInfo getPatchInfoForMethod(String method, String desc) {
        Iterator<UltraFMLMethodPatcherInfo> it = _methodInfos.iterator();
        while (it.hasNext()) {
            UltraFMLMethodPatcherInfo info = it.next();
            if (info.matches(method, desc)) {
                return info;
            }
        }
        return null;
    }
    
    /**
     * Default visit method
     * @param version - the version
     * @param access - public, protected or private
     * @param name - the name of the method we are visiting
     * @param signature - the arguments for the method
     * @param interfaces - the list of implemented interfaces
     */
    @Override
    public void visit(int version, int access, String className, String constructorSignature, String superName, String[] interfaces) {
        super.visit(version, access, className, constructorSignature, superName, interfaces);
    }
    
    /**
     * Override the visit method function to write our custom implementations
     * @param access - public, protected or private
     * @param name - the name of the method being visited
     * @param desc - the arg desc for the method
     * @param signature - the method signature
     * @param execptions - any exceptions the method might throw
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        UltraFMLMethodPatcherInfo patchInfo = getPatchInfoForMethod(name, desc);
        if (patchInfo != null) {
            return patchMethod(patchInfo);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
    
    /**
     * Applies patch for the method identified in the patcher info
     * @param info - the info for the method to parse
     * @return MethodVisitor - the method visitor which contains the bytecode for the method
     */
    protected MethodVisitor patchMethod(UltraFMLMethodPatcherInfo info) {
        LogChannel.INFO.log(UltraFMLPluginHelper.class, "Patching method %s(%s) for class %s", info._methodName, info._methodDesc, info._className);
        MethodVisitor mv = null;
        try {
            mv = info._patchMethod.call();
        }
        catch (Exception e) {
            LogChannel.ERROR.log(UltraFMLPluginHelper.class, "Exception thrown while patching method %s(%s) for class %s with exception: %s", info._methodName, info._methodDesc, info._className, e.getMessage());
            e.printStackTrace();
        }
        return mv;
    }
}