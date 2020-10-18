package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.annotation.EnableEasyrpc;
import com.tiger.easyrpc.core.util.BeanDefinitionRegistryUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

/**
 * 注册Easyrpc各个生命周期组件到spring中
 */
public class EasyrpcComponentRegistrar implements ImportBeanDefinitionRegistrar {
    private BeanDefinitionRegistry beanDefinitionRegistry;
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
        Class<?> introspectedClass = ((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass();
        EnableEasyrpc annotation = introspectedClass.getAnnotation(EnableEasyrpc.class);
        boolean enableClient = annotation.enableClient();
        boolean enableServer = annotation.enableServer();
        if(enableServer){
            EasyRpcManager.getInstance().setServiceScanPath(introspectedClass.getPackage().getName());
            registExporterResolver();
        }
        if(enableClient){
            registFetcherResolver();
        }
        registCloseResolver();
    }

    private void registExporterResolver(){
        BeanDefinitionRegistryUtils.regist(beanDefinitionRegistry, ExporterResolver.class);
    }

    private void registFetcherResolver(){
        BeanDefinitionRegistryUtils.regist(beanDefinitionRegistry,FetcherResolver.class);
    }

    private void registCloseResolver(){
        BeanDefinitionRegistryUtils.regist(beanDefinitionRegistry,CloseResolver.class);
    }
}
