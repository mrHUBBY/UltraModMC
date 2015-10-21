package com.hubby.ultra.plugin;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.hubby.ultra.plugin.UltraFMLPluginHelper.Access;

import net.minecraft.launchwrapper.IClassTransformer;

public class UltraFMLTransformerContainer implements IClassTransformer {

    /**
     * Members
     */
    private String className = "net.minecraft.inventory.Container";
    private String superClassName = null;
    private String classSignature = "()V";
    private String methodName = "detectAndSendChanges";
    private String methodArgs = "()V";
    private Access methodAccess = Access.PUBLIC;
    private String methodSignature = null;
    private String[] methodExceptions = null;
    
    /**
     * Primary transform function
     * @param name - the name of the item
     * @param newName - the new name to give
     * @param bytes - the bytecode
     * @return byte[] - the resulting bytecode
     */
    @Override
    public byte[] transform(String name, String newName, byte[] bytes) {
        if (name.equals(this.className)) {
            return applyTransform(bytes, false);
        }
        return bytes;
    }
    
    /**
     * Handles transforming the world object to update it with the
     * new code we want to run in order for our lights to work
     * @param bytes - the bytes to transform
     * @param obf - are we using obfuscated names or not?
     * @return byte[] - the transformed bytecode
     */
    private byte[] applyTransform(byte[] bytes, boolean obf) {
        System.out.println("**************** UltraMod running InventoryPlayer transform! ***********************");
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);
       
        // search for our node...
        UltraFMLMethodSearchResults<VarInsnNode> results = UltraFMLPluginHelper.searchForNode(classNode, methodName, methodArgs, VarInsnNode.class, ALOAD, 8, 0);
        MethodNode m = results._methodNode;
        AbstractInsnNode targetNode = results._targetNode;
        Iterator<AbstractInsnNode> iter = results._iterator;
        
        AbstractInsnNode newNode = new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/inventory/Slot", "putStack", "(Lnet/minecraft/item/ItemStack;)V", false);
        m.instructions.insertBefore(targetNode, newNode);

        targetNode = newNode;
        newNode = new VarInsnNode(ALOAD, 3);
        m.instructions.insertBefore(targetNode, newNode);
        
        targetNode = newNode;
        newNode = new TypeInsnNode(CHECKCAST, "net/minecraft/inventory/Slot");
        m.instructions.insertBefore(targetNode, newNode);
        
        targetNode = newNode;
        newNode = new MethodInsnNode(INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;", true);
        m.instructions.insertBefore(targetNode, newNode);

        targetNode = newNode;
        newNode = new VarInsnNode(ILOAD, 1);
        m.instructions.insertBefore(targetNode, newNode);
        
        targetNode = newNode;
        newNode = new FieldInsnNode(GETFIELD, "net/minecraft/inventory/Container", "inventorySlots", "Ljava/util/List;");
        m.instructions.insertBefore(targetNode, newNode);
        
        targetNode = newNode;
        newNode = new VarInsnNode(ALOAD, 0);
        m.instructions.insertBefore(targetNode, newNode);
        
        targetNode = newNode;
        newNode = new VarInsnNode(ASTORE, 3);
        m.instructions.insertBefore(targetNode, newNode);
        
        targetNode = newNode;
        newNode = new MethodInsnNode(INVOKESTATIC, "com/hubby/ultra/UltraUtils", "validateItemStack", "(Ljava/lang/Integer;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", false);
        m.instructions.insertBefore(targetNode, newNode);

        targetNode = newNode;
        newNode = new VarInsnNode(ALOAD, 3);
        m.instructions.insertBefore(targetNode, newNode);
        
        targetNode = newNode;
        newNode = new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        m.instructions.insertBefore(targetNode, newNode);
        
        targetNode = newNode;
        newNode = new VarInsnNode(ILOAD, 1);
        m.instructions.insertBefore(targetNode, newNode);
        
        // now that we have inserted our instructions we can now complete the
        // writing of the bytecode for this class
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}