package com.tiger.easyrpc.rpc.api.proxy;

import com.tiger.easyrpc.remote.netty4.NettyClient;
import com.tiger.easyrpc.rpc.api.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.tiger.easyrpc.common.EasyrpcConstant.*;

public class EasyrpcInvocatioinHandler implements InvocationHandler {
    private Logger logger = LoggerFactory.getLogger(EasyrpcInvocatioinHandler.class);
    private String url;
    public EasyrpcInvocatioinHandler(String url){
        this.url = url;
    }
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
        Parameter p = new Parameter();
        p.setClazz(method.getDeclaringClass());
        p.setObjs(args);
        p.setMethod(method);
        NettyClient client = new NettyClient(this.url,p);
        NettyClient.ResultFuture connect = client.connect();
        while (connect.getStatus() != RESULT_BACK_SUC){
            Thread.sleep(500);
        }
        return connect.getResult();
    }
}
