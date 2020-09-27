package com.tiger.easyrpc.core.spring;

import com.tiger.easyrpc.core.annotation.Exporter;
import com.tiger.easyrpc.core.cache.server.ExportServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

public class ExporterResolverHelper implements ApplicationContextInitializer {
    private Logger logger = LoggerFactory.getLogger(ExporterResolverHelper.class);
    private final String DEFAULT_PATH = "./";
    private final String FILE_NAME = "class";
    private final String FILE_SEPARATOR = ".";
    private final String PATH_SEPARATOR = File.separator;
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//        resolver();
//        resolveMethods();
    }

    private void resolver(){
        String rootUrl = DEFAULT_PATH;
        try {
            String filePath = System.getProperty("user.dir");
            addClass(filePath,DEFAULT_PATH);
            Enumeration<URL> urls = getClassLoader().getResources(filePath);
            while(urls.hasMoreElements()){
                URL u = urls.nextElement();
                String path = u.toURI().getPath();
                addClass(path,DEFAULT_PATH);
            }
        }catch (Exception e){
            logger.error("扫描解析服务类异常！",e);
        }
    }

    private ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    private boolean isDefaultPackage(String packageName){
        return DEFAULT_PATH.equals(packageName);
    }

    private void addClass(String path, String packageName) throws MalformedURLException, ClassNotFoundException {
        File f = new File(path);
        File[] fs = f.listFiles();
        for (File ft : fs) {
            String name = ft.getName();
            StringBuilder sb = null;
            if (ft.isFile() && FILE_NAME.equals(name.substring(name.lastIndexOf(FILE_SEPARATOR) + 1, name.length()))) {
                sb = new StringBuilder();
                if (!isDefaultPackage(packageName)) {
                    sb.append(packageName);
                    sb.append(FILE_SEPARATOR);
                }
                String fileName = ft.getName();
                String clazz = sb.append(fileName.substring(0, fileName.indexOf(FILE_SEPARATOR))).toString();
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
                if (!path.endsWith("/")) {
                    sb.append(PATH_SEPARATOR);
                }
                sb.append(name);
                path1 = sb.toString();
                sb.delete(0, path1.length());
                if (!isDefaultPackage(packageName)) {
                    sb.append(packageName);
                    sb.append(FILE_SEPARATOR);
                }
                sb.append(name);
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
                    ExportServiceManager.services.put(serviceInterface.getName()+FILE_SEPARATOR+method.getName(),aClass.newInstance());
                } catch (InstantiationException  | IllegalAccessException  e1 ) {
                    throw new RuntimeException("初始化服务类出错！",e1);
                }
            }
        }
    }

}
