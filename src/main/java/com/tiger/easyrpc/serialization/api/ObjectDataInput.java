package com.tiger.easyrpc.serialization.api;

import java.io.IOException;

public abstract class ObjectDataInput implements DataInput{
    public byte[] readBytes() throws IOException { return null; }

    public abstract  Object readObject();
}
