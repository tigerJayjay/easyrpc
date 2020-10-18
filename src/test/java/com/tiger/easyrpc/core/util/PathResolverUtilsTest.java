package com.tiger.easyrpc.core.util;

import com.tiger.easyrpc.core.Closable;
import com.tiger.easyrpc.core.annotation.Exporter;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class PathResolverUtilsTest {

    @Test
    public  void resolverByInterface(){
        String pack = "com.tiger.easyrpc.core.close";
        Class interClass = Closable.class;
        List<Class> classes = PathResolverUtils.resolverByInterface(pack, interClass);
        Assert.assertEquals(1,classes.size());
    }

    @Test
    public void resolverByAnnotation(){
        String pack = "com.tiger.easyrpc.core.util.testclass";
        Class annoClass = Exporter.class;
        List<Class> classes = PathResolverUtils.resolverByAnnotation(pack, annoClass);
        Assert.assertEquals(1,classes.size());
    }

}
