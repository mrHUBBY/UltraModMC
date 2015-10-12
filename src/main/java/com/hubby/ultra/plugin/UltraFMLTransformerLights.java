package com.hubby.ultra.plugin;

import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ISTORE;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.hubby.utils.HubbyConstants.LogChannel;

import net.minecraft.launchwrapper.IClassTransformer;


// NOTE:
// This code was taken as inspiration from DynamicLights 1.8 which can
// be found at the following link...
// http://www.curse.com/mc-mods/minecraft/227874-dynamic-lights/2231446
// thanks to the author for figuring out the bytecode needed to do this... I am pretty sure he
// used the ASM framework along with the ByteCode Outline perspective which allows you to
// view the ByteCode for any chunk of code appearing in the editor window.

public class UltraFMLTransformerLights implements IClassTransformer {
    
    /**
     * Members
     */
    private String classNameWorld = "aqu";
    private String targetMethodDesc = "(Ldt;Larf;)I";
    private String computeLightValueMethodName = "a";
    private String goalInvokeDesc = "(Latr;Lard;Ldt;)I";

    /**
     * Primary transform function
     * @param name - the name of the item
     * @param newName - the new name to give
     * @param bytes - the bytecode
     * @return byte[] - the resulting bytecode
     */
    public byte[] transform(String name, String newName, byte[] bytes) {
        
        // check for the world class
        if (name.equals(this.classNameWorld)) {
            return applyTransform(bytes, true);
        }
        
        // check for the world class by its real name
        if (name.equals("net.minecraft.world.World")) {
            this.computeLightValueMethodName = "getRawLight";
            this.targetMethodDesc = "(Lnet/minecraft/util/BlockPos;Lnet/minecraft/world/EnumSkyBlock;)I";
            this.goalInvokeDesc = "(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;)I";
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

        LogChannel.INFO.log(UltraFMLTransformerLights.class, "Running transform for light nodes on World class with obfuscation: %s", obf);
        
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);
        
        // find method to inject into
        Iterator<MethodNode> methods = classNode.methods.iterator();
        while(methods.hasNext()) {
            MethodNode m = methods.next();
            
            // look for the matching method by comparing the method name and its arguments
            if (m.name.equals(computeLightValueMethodName) && m.desc.equals(targetMethodDesc)) {
                
                LogChannel.INFO.log(UltraFMLTransformerLights.class, "In target method: %s, pacthing...", computeLightValueMethodName);
                
                /* before patch:
                   0: aload_2       
                   1: getstatic     #136                // Field net/minecraft/world/EnumSkyBlock.SKY:Lnet/minecraft/world/EnumSkyBlock;
                   4: if_acmpne     18
                   7: aload_0       
                   8: aload_1       
                   9: invokevirtual #160                // Method isAgainstSky:(Lnet/minecraft/util/BlockPos;)Z
                  12: ifeq          18
                  15: bipush        15
                  17: ireturn       
                  18: aload_0       
                  19: aload_1       
                  20: invokevirtual #80                 // Method getBlockState:(Lnet/minecraft/util/BlockPos;)Lnet/minecraft/block/state/IBlockState;
                  23: invokeinterface #81,  1           // InterfaceMethod net/minecraft/block/state/IBlockState.getBlock:()Lnet/minecraft/block/Block;
                  28: astore_3      
                  29: aload_3       
                  30: aload_0       
                  31: aload_1       
                  32: invokevirtual #486                // Method net/minecraft/block/Block.getLightValue:(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;)I
                  35: istore        4
                  [... many more...]
                 */
                
                // iterate over instruction list for the target method we are in
                AbstractInsnNode targetNode = null;
                Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                boolean found = false;
                while (iter.hasNext()) {
                    
                    // find the first ASTORE node, it stores the Block reference for the Block.getLightValue call
                    targetNode = (AbstractInsnNode) iter.next();
                    if (targetNode.getOpcode() == ASTORE) {
                        
                        VarInsnNode astore = (VarInsnNode) targetNode;
                        LogChannel.INFO.log(UltraFMLTransformerLights.class, "Found ASTORE node, is writing variable number: %d", astore.var);
                        
                        // go further until ISTORE is hit
                        while (targetNode.getOpcode() != ISTORE) {
                            
                            // look for method instruction node
                            if (targetNode instanceof MethodInsnNode) {
                                MethodInsnNode mNode = (MethodInsnNode) targetNode;
                                LogChannel.INFO.log(UltraFMLTransformerLights.class, "Found target node, opcode: %d, %s %s %s", mNode.getOpcode(), mNode.owner, mNode.name, mNode.desc);
                                found = true;
                                iter.remove();
                                
                                // select next node as target
                                targetNode = iter.next();
                                break;
                            }
                            
                            // iterate to next node as we look for ISTORE opcode
                            targetNode = iter.next();
                            LogChannel.INFO.log(UltraFMLTransformerLights.class, "Node: %s opcode: %d", targetNode, targetNode.getOpcode());
                        }
                        break;
                    }
                }
                
                // if we found the node we were looking for then
                // we can insert our method call right before it like so...
                if (found) {
                    m.instructions.insertBefore(targetNode, new MethodInsnNode(INVOKESTATIC, "com/hubby/ultra/UltraLightHelper", "getLightValue", this.goalInvokeDesc, false));
                }
                break;
            }
        }
        
        /**
         * write out the modified bytecode and return that as the result of this
         * function which will result in our <code>UtlraLightHelper.getLightValue</code>
         * being called
         */
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}