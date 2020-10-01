package com.tiger.easyrpc.rpc.proxy;

import com.tiger.easyrpc.common.SnowflakeUtils;
import com.tiger.easyrpc.core.cache.client.MessageToChannelManager;
import com.tiger.easyrpc.core.metadata.AnnotationMetadata;
import com.tiger.easyrpc.core.metadata.FetcherMetadata;
import com.tiger.easyrpc.core.metadata.MetadataManager;
import com.tiger.easyrpc.core.urlstrategy.RandomStrategy;
import com.tiger.easyrpc.remote.netty4.NettyChannel;
import com.tiger.easyrpc.rpc.Parameter;
import com.tiger.easyrpc.rpc.ResultFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

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
        AnnotationMetadata metadata = MetadataManager.getInstance().getMetadata(System.identityHashCode(proxy));
        FetcherMetadata fetcherMetadata = ((FetcherMetadata)metadata);
        //获取url
        String urlStr = fetcherMetadata.getUrl();
        String version = fetcherMetadata.getVersion();
        String group = fetcherMetadata.getGroup();
        String url = getRandomUrl(urlStr);
        Long mesId = SnowflakeUtils.genId();
        //调用远程方法并返回
        Parameter p = new Parameter(mesId,args,method.getDeclaringClass(),method,version,group);
        NettyChannel channel = new NettyChannel(url);
        MessageToChannelManager.messageToChannel.put(mesId,channel);
        channel.sendMessage(p);
        ResultFuture resultFuture = channel.getResultFuture();
        Object result = resultFuture.getResult();
        MessageToChannelManager.messageToChannel.remove(mesId);
        return result;
    }


    private String getRandomUrl(String url){
        if(StringUtils.isEmpty(url)){
            throw new RuntimeException("远程服务url为空！请检查配置！");
        }
        String[] urls = url.split(COMMON_SYMBOL_DH);
        //暂时时候随机策略，后续添加扩展点机制
        RandomStrategy randomStrategy = new RandomStrategy();
        return randomStrategy.select(urls);
    }
}
