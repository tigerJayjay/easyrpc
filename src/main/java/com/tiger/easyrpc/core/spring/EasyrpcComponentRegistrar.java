package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.util.BeanDefinitionRegistryUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;


public class EasyrpcComponentRegistrar implements ImportBeanDefinitionRegistrar {
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        Class<?> introspectedClass = ((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass();
        EasyRpcManager.getInstance().setServiceScanPath(introspectedClass.getPackage().getName());
        registExporterResolver(beanDefinitionRegistry);
        registFetcherResolver(beanDefinitionRegistry);
        registBeanImporter(beanDefinitionRegistry);
    }

    private void registExporterResolver(BeanDefinitionRegistry registry){
        BeanDefinitionRegistryUtils.regist(registry, ExporterResolver.class);
    }

    private void registFetcherResolver(BeanDefinitionRegistry registry){
        BeanDefinitionRegistryUtils.regist(registry,FetcherResolver.class);
    }

    private void registBeanImporter(BeanDefinitionRegistry registry){
        BeanDefinitionRegistryUtils.regist(registry,EasyrpcComponentImporter.class);
    }
}
