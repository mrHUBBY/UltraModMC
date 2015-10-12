package com.hubby.ultra.plugin;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.hubby.utils.HubbyConstants.LogChannel;

public class UltraFMLPluginHelper {

    public enum Access {
        PUBLIC      (ACC_PUBLIC),
        PROTECTED   (ACC_PROTECTED),
        PRIVATE     (ACC_PRIVATE);
        
        /**
         * Members
         */
        private Integer _opcode;
        
        /**
         * Constructor
         */
        Access(Integer opcode) {
            _opcode = opcode;
        }
         
        /**
         * Returns the underlying opcode for this enum
         * @return Integer - the opcode2
         */
        public Integer getOpcode() {
            return _opcode;
        }
    }
     
    /**
     * Determines if the method identified by name and description
     * exists within the bytecode passed in via the bytes param
     * @param bytes - the bytecode to check
     * @param methodName - the method name we are looking for
     * @param methodDesc - the arguments description for the method we want
     * @return boolean - does the method exist?
     */
    public static boolean doesMethodExist(byte[] bytes, String methodName, String methodDesc) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        while(methods.hasNext()) {
            MethodNode m = methods.next();
            if (m.name.equals(methodName) && m.desc.equals(methodDesc)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if the field identified by name exists in the bytecode passed in
     * @param bytes - the bytecode to search
     * @param fieldName - the name of the field we want
     * @return boolean - does the field exist?
     */
    public static boolean doesFieldExist(byte[] bytes, String fieldName) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        boolean found = false;
        Iterator<FieldNode> fields = classNode.fields.iterator();
        while (fields.hasNext()) {
            FieldNode f = fields.next();
            if (f.name.equals(fieldName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Declares a field within the context of the class visitor passed in
     * @param visitor - the class visitor
     * @param fieldName - the name of the field to declare 
     * @param superName
     * @return
     */
    public static FieldVisitor declareField(ClassVisitor visitor, Access access, String fieldName, String fieldType, Object defaultValue) {
        FieldVisitor fv = null;
        fv = visitor.visitField(access.getOpcode(), fieldName, fieldType, null, defaultValue);
        fv.visitEnd();
        return fv;
    }
    
    /**
     * Attempts to search for the specific instruction node for the specified method
     * @param node - the node with all of the methods
     * @param methodName - the method to search for
     * @param methodArgs - the specific signature of the method we are looking for
     * @param instructionClass - the type of instruction we are looking for
     * @param opcode - the opcode we are looking for
     * @param matchCount - how many matches we need before we are at the target node
     * @param skipCount - how many instructions to advance once we find our target node
     * @return UltraFMLInsnSearchResults - the search results (null if the node could not be found)
     */
    public static <T extends AbstractInsnNode> UltraFMLMethodSearchResults<T> findMethodInstructionNode(ClassNode node, String methodName, String methodArgs, Class<T> instructionClass, Integer opcode, Integer matchCount, Integer skipCount) {

        // iterate over all methods
        Iterator<MethodNode> methods = node.methods.iterator();
        while(methods.hasNext()) {
            
            // look for the matching method by comparing the method name and its arguments
            MethodNode m = methods.next();
            if (m.name.equals(methodName) && m.desc.equals(methodArgs)) {
                
                LogChannel.INFO.log(UltraFMLPluginHelper.class, "In target method: %s, searching for instructions...", methodName);
                
                // iterate over instruction list for the target method we are in
                AbstractInsnNode targetNode = null;
                Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                while (iter.hasNext()) {
                   
                    targetNode = (AbstractInsnNode) iter.next();
                    if (instructionClass.isInstance(targetNode)) {
                        T specificInsn = (T)targetNode;
                        if (specificInsn.getOpcode() == opcode) {
                            
                            // have we encountered enough matches yet?
                            if (--matchCount == 0) {
                                
                                // advance more nodes if we need to
                                while (skipCount > 0) {
                                    targetNode = iter.next();
                                    --skipCount;
                                }
                                
                                // return the results
                                UltraFMLMethodSearchResults<T> results = new UltraFMLMethodSearchResults<T>();
                                results._methodNode = m;
                                results._targetNode = targetNode;
                                results._iterator = iter;
                                return results;
                            }
                        }
                    }
                }
            }
        }
        
        // could not find the instruction
        return null;
    }
}