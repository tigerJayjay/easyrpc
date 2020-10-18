package com.tiger.easyrpc.core.util;

import com.tiger.easyrpc.core.function.ScanConsumer;
import com.tiger.easyrpc.core.spring.EasyrpcClassVisitor;
import org.springframework.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.tiger.easyrpc.common.EasyrpcConstant.COMMON_SYMBOL_XG;
import static com.tiger.easyrpc.common.EasyrpcConstant.COMMON_SYMBOL_YJH;

/**
 *类扫描解析工具类
 */
public class PathResolverUtils {
    private static final String FILE_NAME = "class";
    private static final String jarSpitPath = "/";
    private static final String jarFileSplit = "!";
    private static final String fileHeader = "file:/";
    private static void scan(String pack, ScanConsumer<String,String> consumer){
        String scanPath = pack.replace(COMMON_SYMBOL_YJH,jarSpitPath);
        try {
            Enumeration<URL> urls = getClassLoader().getResources(scanPath);
            while(urls.hasMoreElements()){
                URL u = urls.nextElement();
                String path = u.toURI().getPath();
                if("jar".equals(u.getProtocol())){
                    scanClassJar(u.getFile(),consumer);
                }else{
                    scanClass(path,pack,consumer);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 扫描Jar包
     */
    private static void scanClassJar(String path,ScanConsumer<String,String> consumer) throws IOException, ClassNotFoundException {
        String[] jarUrl = path.split(jarFileSplit);
        String jarPath = jarUrl[0];
        jarPath = jarPath.replace(fileHeader, "");
        String packPath = jarUrl[1];
        if(packPath.startsWith(jarSpitPath))
            packPath = packPath.substring(1);
        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> entries = jarFile.entries();
        while(entries.hasMoreElements()){
            JarEntry jarEntry = entries.nextElement();
            String jarClassPath = jarEntry.getName();

            if(jarClassPath.startsWith(packPath) && !jarClassPath.equals(packPath)){
                String className = jarClassPath.substring(jarClassPath.lastIndexOf(packPath)).
                        replace(jarSpitPath,COMMON_SYMBOL_YJH);
                if(className.endsWith(FILE_NAME)){
                    className = className.substring(0,className.lastIndexOf(COMMON_SYMBOL_YJH));
                    InputStream inputStream = null;
                    try{
                        inputStream = jarFile.getInputStream(jarFile.getEntry(jarClassPath));
                        ClassReader reader = new ClassReader(inputStream);
                        reader.accept(new EasyrpcClassVisitor(consumer,className),ClassReader.SKIP_FRAMES);
                    }finally {
                        if(inputStream != null){
                            inputStream.close();
                        }
                    }
                }

            }
        }
    }

    /**
     * 通过接口获取类的Class列表
     * @param pack 包路径
     * @param interClass 接口Class
     * @return
     */
    public static List<Class> resolverByInterface(String pack,Class interClass){
        Predicate<Object> predicate = obj ->{
            if(obj instanceof String){
                String classPath = ((String) obj).replace(jarSpitPath, COMMON_SYMBOL_YJH);
                if(interClass.getName().equals(classPath)){
                    return true;
                }
            }
            return false;
        };
        return resolver(pack,predicate);
    }

    /**
     * 通过注解获取类的Class列表
     * @param pack 指定包路径
     * @param annoClass 注解Class
     * @return
     */
    public static List<Class> resolverByAnnotation(String pack, Class annoClass){
        Predicate<Object> predicate = clazz ->{
            if(clazz instanceof String){
                String classPath = ((String) clazz).replace(jarSpitPath, COMMON_SYMBOL_YJH);
                if (annoClass.getName().equals(classPath)){
                    return true;
                }
            }
           return false;
        };
        return resolver(pack,predicate);
    }

    private static List<Class> resolver(String pack, Predicate<Object> predicate){
        List<Class> result = new ArrayList<>();
        ScanConsumer<String,String> consumer = (p1,p2) ->{
            boolean test = predicate.test(p1);
            if(test){
                try {
                    result.add(Class.forName(p2));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        scan(pack,consumer);
        return result;
    }

    private static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 扫描目录
     * @param path 目录路径
     * @param packageName 包路径
     * @param consumer 对扫描到的Class执行的操作
     */
    private static void scanClass(String path, String packageName,ScanConsumer<String,String> consumer) throws IOException {
        File f = new File(path);
        File[] fs = f.listFiles();
        for (File ft : fs) {
            String name = ft.getName();
            StringBuilder sb = null;
            if (ft.isFile() && FILE_NAME.equals(name.substring(name.lastIndexOf(COMMON_SYMBOL_YJH) + 1))) {
                sb = new StringBuilder().append(packageName).append(COMMON_SYMBOL_YJH);
                String fileName = ft.getName();
                String clazz = sb.append(fileName.substring(0, fileName.indexOf(COMMON_SYMBOL_YJH))).toString();
                InputStream inputStream = null;
                try{
                    inputStream =new FileInputStream(ft);
                    ClassReader reader = new ClassReader(inputStream);
                    reader.accept(new EasyrpcClassVisitor(consumer,clazz),ClassReader.SKIP_FRAMES);
                }finally {
                    if(inputStream != null){
                        inputStream.close();
                    }
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
                scanClass(path1, packageName1,consumer);
            }
        }
    }

}
