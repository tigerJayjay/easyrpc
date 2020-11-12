package com.tiger.easyrpc.rpc.proxy;

import com.tiger.easyrpc.common.SnowflakeUtils;
import com.tiger.easyrpc.common.SysCacheEnum;
import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.cache.client.MessageToChannelManager;
import com.tiger.easyrpc.core.config.ConsumerConfig;
import com.tiger.easyrpc.core.metadata.AnnotationMetadata;
import com.tiger.easyrpc.core.metadata.FetcherMetadata;
import com.tiger.easyrpc.core.metadata.MetadataManager;
import com.tiger.easyrpc.core.urlstrategy.RandomStrategy;
import com.tiger.easyrpc.registry.cache.CacheManager;
import com.tiger.easyrpc.registry.cache.ICache;
import com.tiger.easyrpc.remote.netty4.NettyChannel;
import com.tiger.easyrpc.rpc.Parameter;
import com.tiger.easyrpc.rpc.ResultFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.tiger.easyrpc.common.EasyrpcConstant.*;

/**
 * jdk代理处理类，通过该类来调用Channel对象进行远程调用并获取远程返回结果
 */
public class EasyrpcInvocatioinHandler implements InvocationHandler {
    private Object o;
    public EasyrpcInvocatioinHandler(){
        o = new Object();
    }
    private Logger logger = LoggerFactory.getLogger(EasyrpcInvocatioinHandler.class);
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return o.toString();
        }else if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return o.hashCode();
        }else if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return o.equals(args[0]);
        }else if (method.getDeclaringClass() == Object.class) {
            return method.invoke(o, args);
        }
        //通过代理对象获取Fetcher元数据信息，用来组装远程服务信息
        AnnotationMetadata metadata = MetadataManager.getInstance().getMetadata(proxy);
        String version = "";
        String group = "";
        String urlStr = "";
        if(metadata!=null){
            //从@Fetcher注解获取服务信息
            FetcherMetadata fetcherMetadata = ((FetcherMetadata)metadata);
            version = fetcherMetadata.getVersion();
            group = fetcherMetadata.getGroup();
            urlStr = fetcherMetadata.getUrl();
        }
        ConsumerConfig consumerConfig = EasyRpcManager.getInstance().getConsumerConfig();
        //从全局配置获取服务信息
        if(StringUtils.isEmpty(version)){
            version = consumerConfig.getVersion() == null ? EMPTY_STR : consumerConfig.getVersion();

        }
        if(StringUtils.isEmpty(group)){
            group = consumerConfig.getGroup() == null ? EMPTY_STR : consumerConfig.getGroup();

        }
        if(StringUtils.isEmpty(urlStr)){
            urlStr  = consumerConfig.getRemoteUrl();

        }
        if(StringUtils.isEmpty(urlStr)){
            ICache cacheProvider = CacheManager.instance().getCacheProvider(SysCacheEnum.serviceurl.getCacheName());
            if(StringUtils.isEmpty(urlStr)){
                //从注册中心获取
                if(cacheProvider != null){
                    String serviceName = method.getDeclaringClass().getName()+COMMON_SYMBOL_MH+version+COMMON_SYMBOL_MH+group;
                    Object arg0 = cacheProvider.get(serviceName);
                    if(arg0 != null)
                        urlStr = String.valueOf(arg0);
                }
            }
        }
        if( urlStr== null){
            throw new RuntimeException("无法获取可用服务，服务地址为空！");
        }
        String url = getRandomUrl(urlStr);
        Long mesId = SnowflakeUtils.genId();
        //调用远程方法并返回
        Parameter p = new Parameter(mesId,args,method.getDeclaringClass(),method,version,group);
        NettyChannel channel = new NettyChannel(url);
        MessageToChannelManager.messageToChannel.put(mesId,channel);
        channel.sendMessage(p);
        ResultFuture resultFuture = channel.getResultFuture();
        Object result = resultFuture.getResult(consumerConfig.getRpcTimeout());
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
