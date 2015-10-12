package com.hubby.ultra.plugin;

import java.util.concurrent.Callable;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

/**
 * Implements the callable interface for supporting method visitor code
 * being passed around
 * @author davidleistiko
 */
public class UltraFMLMethodPatcherCallable implements Callable<MethodVisitor> {
    /**
     * Members
     */
    protected ClassWriter _classWriter;
    
    /**
     * Constructor
     * @param cw
     */
    public UltraFMLMethodPatcherCallable(ClassWriter cw) {
        _classWriter = cw;
    }
    
    /**
     * Call function
     * @return MethodVisitor - the bytecode
     */
    @Override
    public MethodVisitor call() throws Exception {
        return null;
    }
}