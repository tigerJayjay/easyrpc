package com.tiger.easyrpc.config.spring;

import com.easyrpc.config.annotation.Exporter;
import com.easyrpc.config.annotation.ExporterScan;
import com.easyrpc.config.util.BeanDefinitionRegistryUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * 找到标有ExporterScan注解的类，获取注解值，加载指定包中带有Exporter的类放入Set中
 */
public class ExporterScanResolver implements BeanDefinitionRegistryPostProcessor {
    private final String FILE_NAME = "class";
    private final String FILE_SEPARATOR = ".";
    private final String PATH_SEPARATOR = "/";
    private final String DEFAULT_PATH = "./";
    private Set<Class> exporterClass = new HashSet<Class>(256);
    private Map<String,Object> exporterClassMap;
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        System.out.println("postProcessBeanFactory");

        exporterClassMap = configurableListableBeanFactory.getBeansWithAnnotation(ExporterScan.class);
        resolver();
        System.out.println(exporterClass);

        resolveMethods();
    }

    private void resolveMethods(){
        for (Class aClass : exporterClass) {
            System.out.println(aClass.getName());
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                System.out.println(method);

            }
        }
    }


    private void resolver(){
        Object bean = null;
        String rootUrl = DEFAULT_PATH;
        try {
            for(Map.Entry<String,Object> entry:exporterClassMap.entrySet()){
                bean = entry.getValue();
                Class cla = bean.getClass();
                ExporterScan exporterAnno = (ExporterScan)cla.getAnnotation(ExporterScan.class);
                String packages = exporterAnno.packages();
                if(!DEFAULT_PATH.equals(packages)){
                    rootUrl = packages.replace(FILE_SEPARATOR,PATH_SEPARATOR);
                }
                Enumeration<URL> urls = getClassLoader().getResources(rootUrl);
                while(urls.hasMoreElements()){
                    URL u = urls.nextElement();
                    String path = u.getPath();
                    addClass(path,packages);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    private void addClass(String path, String packageName){
        File f = new File(path);
        File[] fs  = f.listFiles();
        for(File ft:fs){
            String name = ft.getName();
            StringBuilder sb = null;
            if(ft.isFile()&& FILE_NAME.equals(name.substring(name.lastIndexOf(".")+1,name.length()))){
                sb = new StringBuilder();
                if(!isDefaultPackage(packageName)){
                    sb.append(packageName);
                    sb.append(FILE_SEPARATOR);
                }
                String fileName = ft.getName();
                String clazz = sb.append(fileName.substring(0,fileName.indexOf(FILE_SEPARATOR))).toString();
                try {
                    Class cls = Class.forName(clazz);
                    if(cls.isAnnotationPresent(Exporter.class)){
                        exporterClass.add(cls);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Class:"+clazz+"not found");
                }
            }else if(ft.isDirectory()){
                String path1 = "";
                String packageName1 = "";
                sb = new StringBuilder();
                sb.append(path);
                if(!path.endsWith("/")){
                    sb.append(PATH_SEPARATOR);
                }
                sb.append(name);
                path1 = sb.toString();
                sb.delete(0,path1.length());
                if(!isDefaultPackage(packageName)){
                    sb.append(packageName);
                    sb.append(FILE_SEPARATOR);
                }
                sb.append(name);
                packageName1 = sb.toString();
                addClass(path1,packageName1);
            }
        }

    }

    private boolean isDefaultPackage(String packageName){
        return DEFAULT_PATH.equals(packageName);
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        System.out.println("postProcessBeanDefinitionRegistry");
        for(Class c:exporterClass){
            BeanDefinitionRegistryUtils.regist(beanDefinitionRegistry,c);
        }
    }

}
