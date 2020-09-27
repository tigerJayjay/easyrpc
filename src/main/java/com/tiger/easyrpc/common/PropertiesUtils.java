package com.tiger.easyrpc.common;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class PropertiesUtils implements EnvironmentAware {
    private static Environment envir;
    @Override
    public void setEnvironment(Environment environment) {
        envir = environment;
    }

    public static String getPropertyValue(String property){
        return envir.getProperty(property);
    }
}
