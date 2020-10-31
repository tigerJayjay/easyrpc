package com.tiger.easyrpc.remote.api;

/**
 * rpc客户端接口
 */
public interface Client {
    /**
     * 关闭客户端
     */
    void close();

    /**
     * 获取客户端状态
     * @return
     */
    int getStatus();
}
