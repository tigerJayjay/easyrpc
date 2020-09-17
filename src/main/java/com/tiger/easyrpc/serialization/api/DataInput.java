package com.tiger.easyrpc.serialization.api;

import java.io.IOException;

/**
 * 抽象序列化层统一数据输入接口
 */
public interface DataInput {
    byte[] readBytes() throws IOException;
}
