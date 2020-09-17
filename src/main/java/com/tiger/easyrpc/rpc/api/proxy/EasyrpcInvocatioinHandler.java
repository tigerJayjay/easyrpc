package com.tiger.easyrpc.rpc.api.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class EasyrpcInvocatioinHandler implements InvocationHandler {
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(proxy, args);
        }
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return proxy.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return proxy.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return proxy.equals(args[0]);
        }
        //调用远程方法并返回
        return null;
    }
}
