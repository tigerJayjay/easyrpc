package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.core.ConsumerConfig;
import com.tiger.easyrpc.core.annotation.Fetcher;
import com.tiger.easyrpc.rpc.api.proxy.ServiceProxy;
import com.tiger.easyrpc.rpc.api.proxy.jdk.JdkProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * 获取标有@Fetcher注解的field，注册为BeanDefinition对象
 */
public class FetcherResolver implements BeanDefinitionRegistryPostProcessor, BeanPostProcessor {
    private BeanDefinitionRegistry registry;
    private BeanFactory beanFactory;
    private Set<Class> fetcherClass = new HashSet<Class>(256);
    private ServiceProxy proxy;
    private ConsumerConfig consumerConfig;
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            Class clazz = bean.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for(Field f:fields){
                f.setAccessible(true);
                if(f.isAnnotationPresent(Fetcher.class)){
                    setConsumberConfig(f);
                    proxy = new JdkProxy();
                    Object dependBean = proxy.getProxy(clazz);
                    f.set(bean,dependBean);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return bean;
    }

    private void setConsumberConfig(Field field){
        if(consumerConfig == null){
            consumerConfig = new ConsumerConfig();
        }
        Fetcher annotation = field.getAnnotation(Fetcher.class);
        String[] url = annotation.url();
        consumerConfig.setRemoteUrl(url);
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        this.beanFactory = configurableListableBeanFactory;
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.registry = beanDefinitionRegistry;
    }
}
