package com.tiger.easyrpc.rpc.api.proxy.jdk;
import com.tiger.easyrpc.rpc.api.proxy.EasyrpcInvocatioinHandler;
import com.tiger.easyrpc.rpc.api.proxy.ServiceProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

public class JdkProxy<T> implements ServiceProxy<T> {
    private String url;
    public JdkProxy(String url){
        this.url = url;
    }
    public T getProxy(T t) {
        Field f = (Field)t;
       return  (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{f.getType()},new EasyrpcInvocatioinHandler(this.url));
    }
}
