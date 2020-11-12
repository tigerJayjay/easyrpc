package com.tiger.easyrpc.rpc.proxy.jdk;

import com.tiger.easyrpc.rpc.proxy.EasyrpcInvocatioinHandler;
import com.tiger.easyrpc.rpc.proxy.ServiceProxy;

import java.lang.reflect.Proxy;

/**
 * Jdk代理，用来代理远程服务
 * @param <T>
 */
public class JdkProxy<T> implements ServiceProxy<T> {
    public T getProxy(T t) {
        Class f = (Class)t;
       return  (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{f},new EasyrpcInvocatioinHandler());
    }
}
