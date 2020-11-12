package com.tiger.easyrpc.core.spring.factorybean;

import com.tiger.easyrpc.rpc.proxy.jdk.JdkProxy;
import org.springframework.beans.factory.FactoryBean;

public class ProxyFactoryBean implements FactoryBean {
    private Class targetType;

    public ProxyFactoryBean(Class targetType){
        this.targetType = targetType;
    }
    @Override
    public Object getObject() throws Exception {
        JdkProxy jdkProxy = new JdkProxy();
        Object serviceProxy = jdkProxy.getProxy(targetType);
        return serviceProxy;
    }

    @Override
    public Class<?> getObjectType() {
        return targetType;
    }
}
