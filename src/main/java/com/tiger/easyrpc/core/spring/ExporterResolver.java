package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.ProviderConfig;
import com.tiger.easyrpc.core.annotation.Exporter;
import com.tiger.easyrpc.core.cache.server.ExportServiceManager;
import com.tiger.easyrpc.core.util.BeanDefinitionRegistryUtils;
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

import java.io.File;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Enumeration;

import static com.tiger.easyrpc.common.EasyrpcConstant.*;

/**
 * 找到标有ExporterScan注解的类，获取注解值，加载指定包中带有Exporter的类放入Set中
 */
public class ExporterResolver implements BeanDefinitionRegistryPostProcessor, ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(ExporterResolver.class);
    private final String FILE_NAME = "class";
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
        String filePath = scanPath.replace(COMMON_SYMBOL_YJH,COMMON_SYMBOL_XG);
        try {
            Enumeration<URL> urls = getClassLoader().getResources(filePath);
            while(urls.hasMoreElements()){
                URL u = urls.nextElement();
                String path = u.toURI().getPath();
                addClass(path,scanPath);
            }
        }catch (Exception e){
            logger.error("扫描解析服务类异常！",e);
        }
    }

    private ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    private void addClass(String path, String packageName) {
        File f = new File(path);
        File[] fs = f.listFiles();
        for (File ft : fs) {
            String name = ft.getName();
            StringBuilder sb = null;
            if (ft.isFile() && FILE_NAME.equals(name.substring(name.lastIndexOf(COMMON_SYMBOL_YJH) + 1, name.length()))) {
                sb = new StringBuilder().append(packageName).append(COMMON_SYMBOL_YJH);
                String fileName = ft.getName();
                String clazz = sb.append(fileName.substring(0, fileName.indexOf(COMMON_SYMBOL_YJH))).toString();
                try {
                    Class cls = Class.forName(clazz);
                    if (cls.isAnnotationPresent(Exporter.class)) {
                        ExportServiceManager.exporterClass.add(cls);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Class:" + clazz + "not found");
                }
            } else if (ft.isDirectory()) {
                String path1 = "";
                String packageName1 = "";
                sb = new StringBuilder();
                sb.append(path);
                if (!path.endsWith(COMMON_SYMBOL_XG)) {
                    sb.append(COMMON_SYMBOL_XG);
                }
                sb.append(name);
                path1 = sb.toString();
                sb.delete(0, path1.length());
                sb.append(packageName).append(COMMON_SYMBOL_YJH).append(name);
                packageName1 = sb.toString();
                addClass(path1, packageName1);
            }
        }
    }


    private void addService(Class aClass){
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
            ExportServiceManager.services.put(serviceInterface.getName()+
                    COMMON_SYMBOL_FH+version+COMMON_SYMBOL_FH+group,aClass);
    }

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
    }
}
