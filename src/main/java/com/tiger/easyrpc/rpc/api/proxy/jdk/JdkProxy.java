package com.tiger.easyrpc.rpc.api.proxy.jdk;

import com.tiger.easyrpc.rpc.api.proxy.EasyrpcInvocatioinHandler;
import com.tiger.easyrpc.rpc.api.proxy.ServiceProxy;

import java.lang.reflect.Proxy;

public class JdkProxy<T> implements ServiceProxy<T> {
    public T getProxy(T t) {
       return  (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{t.getClass()},new EasyrpcInvocatioinHandler());
    }
}
