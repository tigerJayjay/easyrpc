package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.annotation.Fetcher;
import com.tiger.easyrpc.core.metadata.AnnotationMetadata;
import com.tiger.easyrpc.core.metadata.FetcherMetadata;
import com.tiger.easyrpc.core.metadata.MetadataManager;
import com.tiger.easyrpc.core.spring.factorybean.ProxyFactoryBean;
import com.tiger.easyrpc.core.util.BeanDefinitionRegistryUtils;
import com.tiger.easyrpc.registry.RegistryManager;
import com.tiger.easyrpc.rpc.proxy.jdk.JdkProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描bean中是否有被@Fetcher注解的属性，如果有注入远程服务的代理对象
 */
public class FetcherResolver implements BeanDefinitionRegistryPostProcessor, ApplicationListener<ContextRefreshedEvent>, EnvironmentAware, ResourceLoaderAware {
    private Logger logger = LoggerFactory.getLogger(FetcherResolver.class);
    private BeanDefinitionRegistry beanDefinitionRegistry;
    private Environment environment;
    private ResourcePatternResolver resourcePatternResolver;
    private CachingMetadataReaderFactory metadataReaderFactory;
    private String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private AnnotationMetadata setMetadata(Fetcher fetcher,Class type){
        String version = fetcher.version();
        String group = fetcher.group();
        Object urlStr = fetcher.url();
        FetcherMetadata  metadata = new FetcherMetadata(urlStr==null ? null:String.valueOf(urlStr),version,group,null);
        return metadata;
    }

    /**
     * 创建远程服务jdk代理对象
     * @param field
     * @param bean
     * @return
     */
    private Object setService(Field field,Object bean){
        JdkProxy jdkProxy = new JdkProxy();
        Object serviceProxy = jdkProxy.getProxy(field.getType());
        try {
            field.set(bean,serviceProxy);
        } catch (IllegalAccessException e) {
            logger.error("{}注入服务失败！",e);
        }
        return serviceProxy;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //是否开启了注册中心
        if(EasyRpcManager.getInstance().isEnableRegistry()){
            //获取注册中心地址，刷到本地缓存
            RegistryManager.getInstance().flushLocalCache();
        }
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        //扫描springbean，如果属性中配置了@Fetcher注解，设置代理对象
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
                //设置Fetcher元数据信息
                AnnotationMetadata metadata = setMetadata(annotation, declaredField.getType());
                metadata.setSource(o);
                MetadataManager.getInstance().setMetadata(metadata);
            }
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
        String scanPackage = environment.getProperty("easyrpc.client.service.scan");
        if(StringUtils.isEmpty(scanPackage)){
            return;
        }
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(scanPackage) + '/' + DEFAULT_RESOURCE_PATTERN;
        List<Class> classes = getClasses(packageSearchPath);
        for (Class aClass : classes) {
            BeanDefinitionRegistryUtils.registFactoryBean(beanDefinitionRegistry, ProxyFactoryBean.class,aClass);
        }
    }

    private String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(this.environment.resolveRequiredPlaceholders(basePackage));
    }


    /**
     * 获取指定包下所有类的Class
     * @param packagePath 指定包名
     * @return 返回包下Class列表
     */
    private List<Class> getClasses(String packagePath){
        List<Class> result = new ArrayList<>();
        try {
            Resource[] resources = this.resourcePatternResolver.getResources(packagePath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(className);
                        result.add(clazz);
                    } catch (ClassNotFoundException e) {
                        logger.error("getClasses解析class失败！",e);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("getClasses解析class失败！",e);
        }
        return result;
    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }
}
