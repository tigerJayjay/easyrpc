package com.tiger.easyrpc.serialization.api;

import java.io.IOException;

/**
 * 抽象序列化层统一数据输出接口
 */
public interface DataOutput {
    void writeBytes(byte[] bytes) throws IOException;
}
