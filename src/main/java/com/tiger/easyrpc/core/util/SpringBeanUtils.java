package com.tiger.easyrpc.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 获取bean对象工具类
 */
public class SpringBeanUtils implements ApplicationContextAware {
    private  static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       this.applicationContext = applicationContext;
    }

    /**
     * 通过bean类型获取bean实例
     * @param cls bean Class
     * @param <T> bean实例
     * @return
     */
    public static <T> T getBean(Class<T> cls) {
       return applicationContext.getBean(cls);
    }
}
