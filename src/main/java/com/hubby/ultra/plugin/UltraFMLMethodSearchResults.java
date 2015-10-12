package com.hubby.ultra.plugin;

import java.util.Iterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Simple class that stores the results for a method node
 * instruction search
 * @author davidleistiko
 */
public class UltraFMLMethodSearchResults <T extends AbstractInsnNode> {
    public AbstractInsnNode _targetNode;
    public MethodNode _methodNode;
    public Iterator<AbstractInsnNode> _iterator;
}
