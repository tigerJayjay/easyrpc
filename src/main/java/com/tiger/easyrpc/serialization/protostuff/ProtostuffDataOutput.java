package com.tiger.easyrpc.serialization.protostuff;


import com.tiger.easyrpc.serialization.api.ObjectDataOutput;
import com.tiger.easyrpc.serialization.protostuff.util.ProtostuffUtil;

public class ProtostuffDataOutput extends ObjectDataOutput {

    public byte[] writeObject(Object o) {
        byte[] serializer = ProtostuffUtil.serializer(o);
        System.out.println("writeObject()[size:"+serializer.length+"]");
        return serializer;
    }
}
