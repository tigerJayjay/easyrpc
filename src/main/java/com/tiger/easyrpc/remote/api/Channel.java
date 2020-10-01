package com.tiger.easyrpc.remote.api;

public interface Channel {
    void sendMessage(Object o);

    void receiveMessage(Object o);
}
