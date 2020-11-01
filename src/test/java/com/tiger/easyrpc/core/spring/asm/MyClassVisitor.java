package com.tiger.easyrpc.core.spring.asm;

import org.springframework.asm.*;

public class MyClassVisitor extends ClassVisitor {
    public MyClassVisitor() {
        super(Opcodes.ASM4);
    }

    public void visit(int version, int access, String name,
                      String signature, String superName, String[] interfaces) {
        if(interfaces!=null){
            for (int i = 0; i < interfaces.length; i++) {
                System.err.println(interfaces[i]);
            }
        }

    }

    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value) {
        if (name.startsWith("is")) {
            System.out.println(" field name: " + name +"--"+ desc);
        }
        return null;
    }

    public MethodVisitor visitMethod(int access, String name,
                                     String desc, String signature, String[] exceptions) {
        if (name.startsWith("is")) {
            System.out.println(" start with is method: " + name + desc);
        }
        return null;
    }

    public void visitEnd() {
        System.out.println("}");
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        System.out.println(descriptor+visible);
        return null;
    }
}
