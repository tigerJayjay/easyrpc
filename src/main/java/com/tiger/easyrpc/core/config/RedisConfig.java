package com.tiger.easyrpc.core.config;

import java.util.Map;

public class RedisConfig {
    private String sentinelUrl;
    private String masterName;
    private String mode;
    //redis地址
    private String host;
    //端口
    private int port;
    //连接超时时间
    private int timeout;
    //密码
    private String password;
    //连接池配置
    private Map<String,String> pool;

    public String getSentinelUrl() {
        return sentinelUrl;
    }

    public void setSentinelUrl(String sentinelUrl) {
        this.sentinelUrl = sentinelUrl;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, String> getPool() {
        return pool;
    }

    public void setPool(Map<String, String> pool) {
        this.pool = pool;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
