package com.tiger.easyrpc.remote.api;

import com.tiger.easyrpc.remote.RpcException;

/**
 * 数据通道接口
 */
public interface Channel {
    /**
     * 发送信息
     * @param o
     */
    void sendMessage(Object o) throws RpcException;

    /**
     * 接收返回信息
     * @param o
     */
    void receiveMessage(Object o);
}
