package com.tiger.easyrpc.config.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.util.ObjectUtils;

import java.beans.Introspector;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

public class BeanDefinitionRegistryUtils {
    private static Logger logger  = LoggerFactory.getLogger(BeanDefinitionRegistryUtils.class);
    public static void regist(BeanDefinitionRegistry registry, Class<?> clazz){
        if(ObjectUtils.isEmpty(clazz)){
            return;
        }
        regist(registry,clazz,Introspector.decapitalize(clazz.getSimpleName()));
    }

    public static void regist(BeanDefinitionRegistry registry, Class<?> clazz, String beanName){
        if(ObjectUtils.isEmpty(clazz)){
            return;
        }
        if(registry.containsBeanDefinition(beanName)){
            logger.info("{}已经被注册!",beanName);
            return;
        }
        AbstractBeanDefinition abstractBeanDefinition = getBeanDefinition(clazz);
        registry.registerBeanDefinition(beanName,abstractBeanDefinition);
        if(logger.isInfoEnabled()){
            logger.info("注册{}成功",beanName);
        }
    }

    private  static AbstractBeanDefinition getBeanDefinition(Class<?> clazz){
        BeanDefinitionBuilder builder =  BeanDefinitionBuilder.genericBeanDefinition(clazz);
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        return builder.getBeanDefinition();
    }
}
