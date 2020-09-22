package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.core.util.BeanDefinitionRegistryUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;


public class EasyrpcComponentRegistrar implements ImportBeanDefinitionRegistrar {
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
       registExporterResolver(beanDefinitionRegistry);
       registFetcherResolver(beanDefinitionRegistry);
    }

    private void registExporterResolver(BeanDefinitionRegistry registry){
        BeanDefinitionRegistryUtils.regist(registry, ExporterResolver.class);
    }

    private void registFetcherResolver(BeanDefinitionRegistry registry){
        BeanDefinitionRegistryUtils.regist(registry,FetcherResolver.class);
    }
}
