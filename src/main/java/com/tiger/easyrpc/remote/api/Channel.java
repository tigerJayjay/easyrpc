package com.tiger.easyrpc.remote.api;

public interface Channel {
    void sendMessage(Object o) throws Exception;

    void receiveMessage(Object o) throws Exception;
}
