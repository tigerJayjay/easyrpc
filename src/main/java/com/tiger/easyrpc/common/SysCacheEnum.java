package com.tiger.easyrpc.common;

public enum  SysCacheEnum{
    serviceurl("serviceurl");
    private String cacheName;
    SysCacheEnum(String cacheName){
        this.cacheName = cacheName;
    }
    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

}
