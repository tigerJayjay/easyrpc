package com.tiger.easyrpc.core.spring;
import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.annotation.Fetcher;
import com.tiger.easyrpc.core.urlstrategy.RandomStrategy;
import com.tiger.easyrpc.rpc.api.proxy.jdk.JdkProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

import static com.tiger.easyrpc.common.EasyrpcConstant.*;

@Component
public class FetcherResolver implements BeanPostProcessor {
    private Logger logger = LoggerFactory.getLogger(FetcherResolver.class);
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Fetcher annotation = declaredField.getAnnotation(Fetcher.class);
            if(annotation == null){
                continue;
            }
            String url = annotation.url();
            if(!StringUtils.isEmpty(url)){
                url = getRandomUrl(url);
                setService(declaredField,bean,url);
                return bean;
            }
            logger.info("{}的fetcher属性未设置url，使用默认url！",beanName);
            url = EasyRpcManager.getInstance().getClientRemoteUrl();
            if(StringUtils.isEmpty(url)){
                throw new RuntimeException("服务注入失败，未设置服务url！");
            }
            url = getRandomUrl(url);
            setService(declaredField,bean,url);

        }
        return bean;
    }

    private String getRandomUrl(String url){
        String[] urls = url.split(COMMON_SYMBOL_DH);
        //暂时时候随机策略，后续添加扩展点机制
        RandomStrategy randomStrategy = new RandomStrategy();
        return randomStrategy.select(urls);
    }

    private void setService(Field field,Object bean,String url){
        JdkProxy jdkProxy = new JdkProxy(url);
        Object serviceProxy = jdkProxy.getProxy(field);
        try {
            field.set(bean,serviceProxy);
        } catch (IllegalAccessException e) {
            logger.error("{}注入服务失败！",e);
        }
    }

}
