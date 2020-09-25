package com.tiger.easyrpc.rpc.proxy;

import com.tiger.easyrpc.core.cache.client.FetcherServiceManager;
import com.tiger.easyrpc.core.cache.client.MessageToChannelManager;
import com.tiger.easyrpc.core.urlstrategy.RandomStrategy;
import com.tiger.easyrpc.remote.netty4.NettyChannel;
import com.tiger.easyrpc.rpc.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.tiger.easyrpc.common.EasyrpcConstant.COMMON_SYMBOL_DH;


public class EasyrpcInvocatioinHandler implements InvocationHandler {
    private Logger logger = LoggerFactory.getLogger(EasyrpcInvocatioinHandler.class);
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
        //获取url
        String urlStr = FetcherServiceManager.urlCache.get(method.getDeclaringClass().getTypeName());
        String url = getRandomUrl(urlStr);
        //调用远程方法并返回
        Parameter p = new Parameter("1",args,method.getDeclaringClass(),method,"1.0","default");
        NettyChannel channel = new NettyChannel(url);
        MessageToChannelManager.messageToChannel.put("1",channel);
        channel.sendMessage(p);
        NettyChannel.ResultFuture resultFuture = channel.getResultFuture();
        while(!resultFuture.isAccept()){

        }
        return resultFuture.getResult();
    }


    private String getRandomUrl(String url){
        String[] urls = url.split(COMMON_SYMBOL_DH);
        //暂时时候随机策略，后续添加扩展点机制
        RandomStrategy randomStrategy = new RandomStrategy();
        return randomStrategy.select(urls);
    }
}
