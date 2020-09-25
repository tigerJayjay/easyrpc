package com.tiger.easyrpc.rpc.proxy.jdk;

import com.tiger.easyrpc.rpc.proxy.EasyrpcInvocatioinHandler;
import com.tiger.easyrpc.rpc.proxy.ServiceProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

public class JdkProxy<T> implements ServiceProxy<T> {
    public T getProxy(T t) {
        Field f = (Field)t;
       return  (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{f.getType()},new EasyrpcInvocatioinHandler());
    }
}
