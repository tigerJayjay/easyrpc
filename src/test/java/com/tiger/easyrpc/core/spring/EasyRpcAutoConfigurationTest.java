package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.core.spring.asm.MyClassVisitor;
import org.junit.Test;
import org.springframework.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class EasyRpcAutoConfigurationTest {

    @Test
    public void AsmTest() throws IOException {
        File file = new File("D:\\自定义\\easyrpc\\target\\classes\\com\\tiger\\easyrpc\\core\\spring\\EasyRpcAutoConfiguration.class");
        FileInputStream inputStream = new FileInputStream(file);
        ClassReader reader = new ClassReader(inputStream);
        reader.accept(new MyClassVisitor(),ClassReader.SKIP_FRAMES);

    }
}
