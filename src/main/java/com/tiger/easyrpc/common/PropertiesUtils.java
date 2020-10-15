package com.tiger.easyrpc.common;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * 环境属性获取工具类
 */
public class PropertiesUtils implements EnvironmentAware {
    private static Environment envir;
    @Override
    public void setEnvironment(Environment environment) {
        envir = environment;
    }

    /**
     * 获取属性值
     * @param property 属性名
     * @return
     */
    public static String getPropertyValue(String property){
        return envir.getProperty(property);
    }
}
