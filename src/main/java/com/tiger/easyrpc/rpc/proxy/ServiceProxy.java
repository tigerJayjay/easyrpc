package com.tiger.easyrpc.rpc.proxy;

/**
 * 代理接口，通过实现该接口，指定具体代理方式
 * @param <T>
 */
public interface ServiceProxy<T> {
    T getProxy(T t);
}
