package com.tiger.easyrpc.serialization.protostuff;


import com.tiger.easyrpc.serialization.api.ObjectDataInput;
import com.tiger.easyrpc.serialization.protostuff.util.ProtostuffUtil;

public class ProtostuffDataInput extends ObjectDataInput {

    private byte[] bytes;
    private Class clazz;

    public ProtostuffDataInput(byte[] bytes,Class clazz){
        this.bytes = bytes;
        this.clazz = clazz;
    }
    @Override
    public Object readObject() {
        return ProtostuffUtil.deserializer(bytes,clazz);
    }
}
