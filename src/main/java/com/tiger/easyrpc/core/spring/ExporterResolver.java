package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.annotation.Exporter;
import com.tiger.easyrpc.core.cache.server.ExportServiceManager;
import com.tiger.easyrpc.core.util.BeanDefinitionRegistryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import static com.tiger.easyrpc.common.EasyrpcConstant.COMMON_SYMBOL_XG;
import static com.tiger.easyrpc.common.EasyrpcConstant.COMMON_SYMBOL_YJH;

/**
 * 找到标有ExporterScan注解的类，获取注解值，加载指定包中带有Exporter的类放入Set中
 */
public class ExporterResolver implements BeanDefinitionRegistryPostProcessor {
    private Logger logger = LoggerFactory.getLogger(ExporterResolver.class);
    private final String FILE_NAME = "class";
    public ExporterResolver(){
    }
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }


    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        resolver();
        resolveMethods();
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

    private void addClass(String path, String packageName) throws MalformedURLException, ClassNotFoundException {
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


    private void resolveMethods(){
        for (Class aClass : ExportServiceManager.exporterClass) {
            Type[] genericInterfaces = aClass.getGenericInterfaces();
            if(genericInterfaces.length == 0){
                logger.info("服务类{}需要实现一个服务接口",aClass.getName());
                continue;
            }
            Class serviceInterface = (Class)genericInterfaces[0];
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                try {
                    ExportServiceManager.services.put(serviceInterface.getName()+COMMON_SYMBOL_YJH+method.getName(),aClass.newInstance());
                } catch (InstantiationException  | IllegalAccessException  e1 ) {
                    throw new RuntimeException("初始化服务类出错！",e1);
                }
            }
        }
    }
}
