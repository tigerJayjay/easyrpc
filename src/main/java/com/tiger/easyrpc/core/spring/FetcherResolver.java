package com.tiger.easyrpc.core.spring;
import com.tiger.easyrpc.core.ConsumerConfig;
import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.annotation.Fetcher;
import com.tiger.easyrpc.core.metadata.AnnotationMetadata;
import com.tiger.easyrpc.core.metadata.FetcherMetadata;
import com.tiger.easyrpc.core.metadata.MetadataManager;
import com.tiger.easyrpc.rpc.proxy.jdk.JdkProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

public class FetcherResolver implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private Logger logger = LoggerFactory.getLogger(FetcherResolver.class);

    private AnnotationMetadata setMetadata(Fetcher fetcher,Object service){
        ConsumerConfig consumerConfig = EasyRpcManager.getInstance().getConsumerConfig();
        String url = fetcher.url();
        //优先使用Fetcher注解内配置，如果Fetcher未配置则使用全局配置
        if(StringUtils.isEmpty(url)){
            url = consumerConfig.getRemoteUrl();
        }
        if(StringUtils.isEmpty(url)){
            throw new RuntimeException("服务注入失败，未设置服务url！");
        }
        String version = fetcher.version();
        if(StringUtils.isEmpty(version)){
            version = consumerConfig.getVersion();
        }
        String group = fetcher.group();
        if(StringUtils.isEmpty(group)){
            group = consumerConfig.getGroup();
        }
        FetcherMetadata  metadata = new FetcherMetadata(url,version,group,service);
        return metadata;
    }

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
        String[] beanNames = this.applicationContext.getBeanDefinitionNames();
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
                AnnotationMetadata metadata = setMetadata(annotation, o);
                metadata.setSource(o);
                MetadataManager.getInstance().setMetadata(metadata);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
