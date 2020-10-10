package com.tiger.easyrpc.registry.cache;

public enum CacheTypeEnum {
    Redis("redis"),
    Local("local");
    private String type;
    CacheTypeEnum(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
