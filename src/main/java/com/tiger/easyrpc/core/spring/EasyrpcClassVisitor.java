package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.core.function.ScanConsumer;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Opcodes;

public class EasyrpcClassVisitor extends ClassVisitor {
    private ScanConsumer<String,String> consumer;
    private String sourceClass;
    public EasyrpcClassVisitor() {
        super(Opcodes.ASM4);
    }

    public  EasyrpcClassVisitor(ScanConsumer<String,String> consumer,String sourceClass){
        super(Opcodes.ASM4);
        this.consumer = consumer;
        this.sourceClass = sourceClass;
    }


    public void visit(int version, int access, String name,
                      String signature, String superName, String[] interfaces) {
        if(interfaces!=null){
            for (int i = 0; i < interfaces.length; i++) {
                consumer.accept(interfaces[i],sourceClass);
            }
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        String annoName = descriptor.substring(1,descriptor.length()-1);
        consumer.accept(annoName,sourceClass);
        return null;
    }


}
