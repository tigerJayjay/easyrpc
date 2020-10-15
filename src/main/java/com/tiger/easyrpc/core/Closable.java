package com.tiger.easyrpc.core;

/**
 * 服务关闭接口，通过实现此接口可以在服务关闭时添加操作
 */
public interface Closable {
    void close();
}
