package com.tiger.easyrpc.config.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.util.ObjectUtils;

import java.beans.Introspector;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

public class BeanDefinitionRegistryUtils {
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
        AbstractBeanDefinition abstractBeanDefinition = getBeanDefinition(clazz);
        registry.registerBeanDefinition(beanName,abstractBeanDefinition);
    }

    private  static AbstractBeanDefinition getBeanDefinition(Class<?> clazz){
        BeanDefinitionBuilder builder =  rootBeanDefinition(clazz);
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        return builder.getBeanDefinition();
    }
}
