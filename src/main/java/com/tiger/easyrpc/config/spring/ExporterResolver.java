package com.tiger.easyrpc.config.spring;

import com.tiger.easyrpc.config.cache.server.ExportServiceManager;
import com.tiger.easyrpc.config.util.BeanDefinitionRegistryUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * 找到标有ExporterScan注解的类，获取注解值，加载指定包中带有Exporter的类放入Set中
 */
public class ExporterResolver implements BeanDefinitionRegistryPostProcessor {
    public ExporterResolver(){
    }
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        System.out.println("aaa");
    }


    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        for(Class c:ExportServiceManager.exporterClass){
            BeanDefinitionRegistryUtils.regist(beanDefinitionRegistry,c);
        }
    }

}
