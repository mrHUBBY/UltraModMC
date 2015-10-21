package com.hubby.ultra.plugin;

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.hubby.ultra.plugin.UltraFMLPluginHelper.Access;

import net.minecraft.launchwrapper.IClassTransformer;

public class UltraFMLTransformerInventoryPlayer implements IClassTransformer {

    /**
     * Members
     */
    private String className = "net.minecraft.entity.player.InventoryPlayer";
    private String superClassName = "net.minecraft.inventory.IInventory";
    private String classSignature = "(Lnet/minecraft/entity/player/EntityPlayer;)V";
    private String methodName = "setInventorySlotContents";
    private String methodArgs = "(ILnet/minecraft/item/ItemStack;)V";
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
        UltraFMLMethodSearchResults<VarInsnNode> results = UltraFMLPluginHelper.searchForNode(classNode, methodName, methodArgs, VarInsnNode.class, ASTORE, 2, 3);
        MethodNode m = results._methodNode;
        AbstractInsnNode targetNode = results._targetNode;
        Iterator<AbstractInsnNode> iter = results._iterator;
        
        // now we need to insert our patch nodes in reverse order
        // so that we can take advantage of the  'insertBefore' method
        AbstractInsnNode newNode = new MethodInsnNode(INVOKESTATIC, "com/hubby/ultra/UltraUtils", "onPlayerInventorySlotContentsChanged", "(Ljava/lang/Integer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;[Lnet/minecraft/item/ItemStack;)V", false);
        m.instructions.insertBefore(targetNode, newNode);

        targetNode = newNode;
        newNode = new VarInsnNode(ALOAD, 3);
        m.instructions.insertBefore(targetNode, newNode);
        
        targetNode = newNode;
        newNode = new VarInsnNode(ALOAD, 2);
        m.instructions.insertBefore(targetNode, newNode);
        
        targetNode = newNode;
        newNode = new InsnNode(AALOAD);
        m.instructions.insertBefore(targetNode, newNode);
        
        targetNode = newNode;
        newNode = new VarInsnNode(ILOAD, 1);
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

// THIS IS CODE THAT CAN BE SAMPLED WHEN THE DESIRE IS TO MODIFY BYTECODE 
// IN SUCH A WAY THAT WE WANT TO REPLACE THE DEFINITION OF A DECLARED METHOD,
// WITH A NEW DEFINITION DESCRIBED IN BYTECODE 
// (this would replace the body of the applyTransform function above)

//System.out.println("**************** UltraMod running InventoryPlayer transform! *********************** ");
//ClassNode classNode = new ClassNode();
//ClassReader classReader = new ClassReader(bytes);
//classReader.accept(classNode, ClassReader.SKIP_FRAMES);
//
//ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
//UltraFMLMethodPatcherCallable methodPatcherCallable = new UltraFMLMethodPatcherCallable(cw) {
//@Override
//public MethodVisitor call() {
//  MethodVisitor mv = null;
//  mv = _classWriter.visitMethod(methodAccess.getOpcode(), methodName, methodArgs, methodSignature, methodExceptions);
//  mv.visitCode();
//  Label l0 = new Label();
//  mv.visitLabel(l0);
//  mv.visitLineNumber(1548, l0);
//  mv.visitVarInsn(ALOAD, 0);
//  mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/player/InventoryPlayer", "mainInventory", "[Lnet/minecraft/item/ItemStack;");
//  mv.visitVarInsn(ASTORE, 3);
//  Label l1 = new Label();
//  mv.visitLabel(l1);
//  mv.visitLineNumber(1550, l1);
//  mv.visitVarInsn(ILOAD, 1);
//  mv.visitVarInsn(ALOAD, 3);
//  mv.visitInsn(ARRAYLENGTH);
//  Label l2 = new Label();
//  mv.visitJumpInsn(IF_ICMPLT, l2);
//  Label l3 = new Label();
//  mv.visitLabel(l3);
//  mv.visitLineNumber(1552, l3);
//  mv.visitVarInsn(ILOAD, 1);
//  mv.visitVarInsn(ALOAD, 3);
//  mv.visitInsn(ARRAYLENGTH);
//  mv.visitInsn(ISUB);
//  mv.visitVarInsn(ISTORE, 1);
//  Label l4 = new Label();
//  mv.visitLabel(l4);
//  mv.visitLineNumber(1553, l4);
//  mv.visitVarInsn(ALOAD, 0);
//  mv.visitFieldInsn(GETFIELD, "net/minecraft/entity/player/InventoryPlayer", "armorInventory", "[Lnet/minecraft/item/ItemStack;");
//  mv.visitVarInsn(ASTORE, 3);
//  Label l5 = new Label();
//  mv.visitLabel(l5);
//  mv.visitLineNumber(1554, l5);
//  mv.visitVarInsn(ILOAD, 1);
//  mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
//  mv.visitVarInsn(ALOAD, 3);
//  mv.visitVarInsn(ILOAD, 1);
//  mv.visitInsn(AALOAD);
//  mv.visitVarInsn(ALOAD, 2);
//  mv.visitMethodInsn(INVOKESTATIC, "com/hubby/ultra/UltraUtils", "onPlayerInventoryArmorChanged", "(Ljava/lang/Integer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V", false);
//  Label l6 = new Label();
//  mv.visitLabel(l6);
//  mv.visitLineNumber(1555, l6);
//  Label l7 = new Label();
//  mv.visitJumpInsn(GOTO, l7);
//  mv.visitLabel(l2);
//  mv.visitLineNumber(1557, l2);
//  mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {"[Lnet/minecraft/item/ItemStack;"}, 0, null);
//  mv.visitVarInsn(ILOAD, 1);
//  mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
//  mv.visitVarInsn(ALOAD, 3);
//  mv.visitVarInsn(ILOAD, 1);
//  mv.visitInsn(AALOAD);
//  mv.visitVarInsn(ALOAD, 2);
//  mv.visitMethodInsn(INVOKESTATIC, "com/hubby/ultra/UltraUtils", "onPlayerInventorySlotContentsChanged", "(Ljava/lang/Integer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V", false);
//  mv.visitLabel(l7);
//  mv.visitLineNumber(1560, l7);
//  mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
//  mv.visitVarInsn(ALOAD, 3);
//  mv.visitVarInsn(ILOAD, 1);
//  mv.visitVarInsn(ALOAD, 2);
//  mv.visitInsn(AASTORE);
//  Label l8 = new Label();
//  mv.visitLabel(l8);
//  mv.visitLineNumber(1561, l8);
//  mv.visitInsn(RETURN);
//  Label l9 = new Label();
//  mv.visitLabel(l9);
//  mv.visitLocalVariable("this", "Lnet/minecraft/entity/player/InventoryPlayer;", null, l0, l9, 0);
//  mv.visitLocalVariable("index", "I", null, l0, l9, 1);
//  mv.visitLocalVariable("stack", "Lnet/minecraft/item/ItemStack;", null, l0, l9, 2);
//  mv.visitLocalVariable("var3", "[Lnet/minecraft/item/ItemStack;", null, l1, l9, 3);
//  mv.visitMaxs(3, 4);
//  mv.visitEnd();
//  return mv;
//}
//};
//
//// Build our patch infos and patch the method
//UltraFMLMethodPatcherInfo patchInfo = new UltraFMLMethodPatcherInfo(className, superClassName, "setInventorySlotContents", "(ILnet/minecraft/item/ItemStack;)V", methodPatcherCallable);
//List infos = new ArrayList<UltraFMLMethodPatcherInfo>();
//infos.add(patchInfo);
//UltraFMLMethodPatcher patcher = new UltraFMLMethodPatcher(cw, infos);
//patcher.visit(V1_6, Access.PROTECTED.getOpcode(), className, classSignature, superClassName, null);
//
//// Finally accept the changes
//classReader.accept(patcher, ClassReader.EXPAND_FRAMES);
//
//// complete the class writer
////cw.visitEnd();
//
//StringWriter sw = new StringWriter();
//PrintWriter pw = new PrintWriter(sw);
//CheckClassAdapter.verify(classReader, true, pw);
//System.out.println(sw.toString());
//
//return cw.toByteArray();