package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.annotation.Fetcher;
import com.tiger.easyrpc.core.config.ConsumerConfig;
import com.tiger.easyrpc.core.metadata.AnnotationMetadata;
import com.tiger.easyrpc.core.metadata.FetcherMetadata;
import com.tiger.easyrpc.core.metadata.MetadataManager;
import com.tiger.easyrpc.registry.RegistryManager;
import com.tiger.easyrpc.rpc.proxy.jdk.JdkProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

import static com.tiger.easyrpc.common.EasyrpcConstant.EMPTY_STR;

/**
 * 扫描bean中是否有被@Fetcher注解的属性，如果有注入远程服务的代理对象
 */
public class FetcherResolver implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(FetcherResolver.class);

    private AnnotationMetadata setMetadata(Fetcher fetcher,Class type){
        ConsumerConfig consumerConfig = EasyRpcManager.getInstance().getConsumerConfig();
        String version = fetcher.version();
        if(StringUtils.isEmpty(version)){
            version = consumerConfig.getVersion() == null ? EMPTY_STR : consumerConfig.getVersion();
        }
        String group = fetcher.group();
        if(StringUtils.isEmpty(group)){
            group = consumerConfig.getGroup() == null ? EMPTY_STR : consumerConfig.getGroup();
        }
        Object urlStr = null;
        //获取url,如果未在注解内指定则从全局配置获取url，全局未指定则从注册中心获取,优先级注解>全局配置>注册中心
        //从全局配置获取
        if(!StringUtils.isEmpty(consumerConfig.getRemoteUrl())){
            urlStr = consumerConfig.getRemoteUrl();
        }
        //从注解获取
        if(!StringUtils.isEmpty(fetcher.url())){
            urlStr = fetcher.url();
        }
        FetcherMetadata  metadata = new FetcherMetadata(urlStr==null ? null:String.valueOf(urlStr),version,group,null);
        return metadata;
    }

    /**
     * 创建远程服务jdk代理对象
     * @param field
     * @param bean
     * @return
     */
    private Object setService(Field field,Object bean){
        JdkProxy jdkProxy = new JdkProxy();
        Object serviceProxy = jdkProxy.getProxy(field);
        try {
            field.set(bean,serviceProxy);
        } catch (IllegalAccessException e) {
            logger.error("{}注入服务失败！",e);
        }
        return serviceProxy;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //是否开启了注册中心
        if(EasyRpcManager.getInstance().isEnableRegistry()){
            //获取注册中心地址，刷到本地缓存
            RegistryManager.getInstance().flushLocalCache();
        }
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Field[] declaredFields = bean.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Fetcher annotation = declaredField.getAnnotation(Fetcher.class);
                if(annotation == null){
                    continue;
                }
                Object o = setService(declaredField, bean);
                //设置Fetcher元数据信息
                AnnotationMetadata metadata = setMetadata(annotation, declaredField.getType());
                metadata.setSource(o);
                MetadataManager.getInstance().setMetadata(metadata);
            }
        }

    }
}
