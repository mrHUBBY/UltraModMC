package com.hubby.ultra.plugin;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.hubby.ultra.plugin.UltraFMLPluginHelper.Access;

import net.minecraft.launchwrapper.IClassTransformer;

/**
 * This class overrides the RenderItem class in Minecraft which is responsible for
 * rendering blocks and items for the inventory view as well as some other stuff
 * we're not too concerned with here
 * @author davidleistiko
 */
public class UltraFMLTransformerRenderItem implements IClassTransformer {

    /**
     * Members
     */
    private String className = "net.minecraft.client.renderer.entity.RenderItem";
    private String superClassName = "net.minecraft.client.resources.IResourceManagerReloadListener";
    private String classSignature = "(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/resources/model/ModelManager;)V";
    private String methodName = "renderItemIntoGUI";
    private String methodArgs = "(Lnet/minecraft/item/ItemStack;II)V";
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
        UltraFMLMethodSearchResults<VarInsnNode> results = UltraFMLPluginHelper.searchForNode(classNode, methodName, methodArgs, VarInsnNode.class, ALOAD, 5, 0);
        MethodNode m = results._methodNode;
        AbstractInsnNode targetNode = results._targetNode;
        Iterator<AbstractInsnNode> iter = results._iterator;
        
        AbstractInsnNode newNode = new MethodInsnNode(INVOKEVIRTUAL, "com/hubby/utils/HubbyConstants$OverrideColor", "attemptApplyColor", "()V", false);
        m.instructions.insertBefore(targetNode, newNode);
                
        targetNode = newNode;
        newNode = new FieldInsnNode(GETSTATIC, "com/hubby/utils/HubbyConstants$OverrideColor", "RENDER_ITEM_INTO_GUI", "Lcom/hubby/utils/HubbyConstants$OverrideColor;");
        m.instructions.insertBefore(targetNode, newNode);
        
        // now that we have inserted our instructions we can now complete the
        // writing of the bytecode for this class
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}