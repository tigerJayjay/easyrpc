package com.tiger.easyrpc.serialization.protostuff;


import com.tiger.easyrpc.serialization.api.ObjectDataOutput;
import com.tiger.easyrpc.serialization.protostuff.util.ProtostuffUtil;

public class ProtostuffDataOutput extends ObjectDataOutput {

    public byte[] writeObject(Object o) {
        return ProtostuffUtil.serializer(o);
    }
}
