package com.tiger.easyrpc.common;

/**
 * redis模式
 */
public enum RedisMode {
    Single("single"),
    Sentinel("sentinel"),
    Cluster("cluster");


    private String modeName;
    RedisMode(String modeName){
        this.modeName = modeName;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

}
