package com.tiger.easyrpc.rpc.proxy;

public interface ServiceProxy<T> {
    T getProxy(T t);
}
