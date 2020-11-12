package com.tiger.easyrpc.rpc.proxy.jdk;

import com.tiger.easyrpc.rpc.proxy.ServiceProxy;
import com.tiger.easyrpc.rpc.proxy.jdk.uses.JdkProxyUse;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

public class JdkProxyTest {

    @Test
    public void getProxyTest() throws NoSuchFieldException {
        JdkProxy jdkProxy = new JdkProxy();
        Object proxy = jdkProxy.getProxy(ServiceProxy.class);
        Assert.assertTrue(proxy instanceof ServiceProxy);

        Class aClass = JdkProxyUse.class;
        Field a = aClass.getDeclaredField("a");
        JdkProxy jdkProxy1 = new JdkProxy();
        Object proxy1 = jdkProxy1.getProxy(a.getType());
        Assert.assertTrue(proxy1 instanceof ServiceProxy);

    }
}
