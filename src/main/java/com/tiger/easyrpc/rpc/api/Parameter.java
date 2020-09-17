package com.tiger.easyrpc.rpc.api;

import java.io.Serializable;
import java.lang.reflect.Method;

public  class Parameter implements Serializable {
    private Object[] objs;
    private Class<?> clazz;
    private Method method;
    private String version;
    private String group;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Object[] getObjs() {
        return objs;
    }

    public void setObjs(Object[] objs) {
        this.objs = objs;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
}
