package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.common.URLUtils;
import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.ProviderConfig;
import com.tiger.easyrpc.core.annotation.Exporter;
import com.tiger.easyrpc.core.cache.server.ExportServiceManager;
import com.tiger.easyrpc.core.util.BeanDefinitionRegistryUtils;
import com.tiger.easyrpc.core.util.PathResolverUtils;
import com.tiger.easyrpc.registry.RegistryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.List;

import static com.tiger.easyrpc.common.EasyrpcConstant.COMMON_SYMBOL_MH;
import static com.tiger.easyrpc.common.EasyrpcConstant.EMPTY_STR;

/**
 * 找到标有ExporterScan注解的类，获取注解值，加载指定包中带有Exporter的类放入Set中
 */
public class ExporterResolver implements BeanDefinitionRegistryPostProcessor, ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(ExporterResolver.class);
    public ExporterResolver(){
    }
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }


    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        resolver();
        for(Class c:ExportServiceManager.exporterClass){
            BeanDefinitionRegistryUtils.regist(beanDefinitionRegistry,c);
        }
    }

    private void resolver(){
        String scanPath = EasyRpcManager.getInstance().getServiceScanPath();
        List<Class> classes = PathResolverUtils.resolverByAnnotation(scanPath, Exporter.class);
        ExportServiceManager.exporterClass.addAll(classes);
    }

    private void addService(Class aClass){
        //获取接口
        Type[] genericInterfaces = aClass.getGenericInterfaces();
        if(genericInterfaces.length == 0){
            logger.info("服务类{}需要实现一个服务接口",aClass.getName());
            throw new RuntimeException("{}服务类需要实现接口！");
        }
        Exporter annotation = (Exporter) aClass.getAnnotation(Exporter.class);
        String group = annotation.group();
        String version = annotation.version();
        ProviderConfig providerConfig = EasyRpcManager.getInstance().getProviderConfig();
        if(StringUtils.isEmpty(version)){
            version = providerConfig.getVersion() == null ? EMPTY_STR : providerConfig.getVersion();
        }
        if(StringUtils.isEmpty(group)){
            group = providerConfig.getGroup() == null ? EMPTY_STR : providerConfig.getGroup();
        }
        Class serviceInterface = (Class)genericInterfaces[0];
        try {

            if(EasyRpcManager.getInstance().isEnableRegistry()){
                //注册中心发布
                RegistryManager.getInstance().regist(serviceInterface.getName()+
                        COMMON_SYMBOL_MH+version+COMMON_SYMBOL_MH+group,URLUtils.getLocalServerUrlAndPort());
            }
            ExportServiceManager.services.put(serviceInterface.getName()+
                    COMMON_SYMBOL_MH+version+COMMON_SYMBOL_MH+group,aClass);
        }  catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    /**
     * 当接收到服务启动通知，发布服务并保存服务类信息
     * @param contextRefreshedEvent
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            Exporter annotation = bean.getClass().getAnnotation(Exporter.class);
            if(annotation == null){
                continue;
            }
            this.addService(bean.getClass());
        }
        if(EasyRpcManager.getInstance().isEnableRegistry()){
            //服务启动，发布服务注册事件，刷新客户端本地缓存
            RegistryManager.getInstance().publishUrlChange();
        }
    }
}
