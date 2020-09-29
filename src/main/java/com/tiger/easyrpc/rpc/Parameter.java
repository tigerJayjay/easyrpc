package com.tiger.easyrpc.rpc;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

public  class Parameter implements Serializable {
    private Long mesId;
    private Object[] objs;
    private Class<?> clazz;
    private Method method;
    private String version;
    private String group;

    public Parameter(){}
    public Parameter(Long mesId, Object[] objs, Class<?> clazz, Method method, String version, String group) {
        this.mesId = mesId;
        this.objs = objs;
        this.clazz = clazz;
        this.method = method;
        this.version = version;
        this.group = group;
    }

    public Long getMesId() {
        return mesId;
    }

    public void setMesId(Long mesId) {
        this.mesId = mesId;
    }

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

    @Override
    public String toString() {
        return "Parameter{" +
                "mesId='" + mesId + '\'' +
                ", objs=" + Arrays.toString(objs) +
                ", clazz=" + clazz +
                ", method=" + method +
                ", version='" + version + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
