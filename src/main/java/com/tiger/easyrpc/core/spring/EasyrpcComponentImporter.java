package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.common.PropertiesUtils;
import com.tiger.easyrpc.core.ConsumerConfig;
import com.tiger.easyrpc.core.ProviderConfig;
import com.tiger.easyrpc.core.RegistryConfig;
import com.tiger.easyrpc.core.util.BeanDefinitionRegistryUtils;
import com.tiger.easyrpc.core.util.SpringBeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

public class EasyrpcComponentImporter implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        BeanDefinitionRegistryUtils.regist(beanDefinitionRegistry, PropertiesUtils.class);
        BeanDefinitionRegistryUtils.regist(beanDefinitionRegistry, EasyRpcServicePublishListener.class);
        BeanDefinitionRegistryUtils.regist(beanDefinitionRegistry, SpringBeanUtils.class);
        BeanDefinitionRegistryUtils.regist(beanDefinitionRegistry, ProviderConfig.class);
        BeanDefinitionRegistryUtils.regist(beanDefinitionRegistry, ConsumerConfig.class);
        BeanDefinitionRegistryUtils.regist(beanDefinitionRegistry, RegistryConfig.class);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
