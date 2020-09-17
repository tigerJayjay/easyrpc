package com.tiger.easyrpc.rpc.api.proxy;

public interface ServiceProxy<T> {
    T getProxy(T t);
}
