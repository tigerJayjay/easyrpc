package com.tiger.easyrpc.demo.provider.producers;

import com.tiger.easyrpc.core.annotation.Exporter;
import com.tiger.easyrpc.demo.interfaces.ITest;

@Exporter
public class ProducerTest implements ITest {
    @Override
    public String test(String a) {
        return "Hello World";
    }
}
