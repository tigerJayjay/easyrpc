package com.tiger.easyrpc.serialization.api;

import java.io.IOException;

public abstract class ObjectDataOutput implements DataOutput {
   public abstract  byte[] writeObject(Object o);

    public void writeBytes(byte[] bytes) throws IOException {}
}
