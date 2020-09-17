package com.tiger.easyrpc.serialization.protostuff;

import com.easyrpc.serialization.api.ObjectDataOutput;
import com.easyrpc.serialization.protostuff.util.ProtostuffUtil;

public class ProtostuffDataOutput extends ObjectDataOutput {

    public byte[] writeObject(Object o) {
        return ProtostuffUtil.serializer(o);
    }
}
