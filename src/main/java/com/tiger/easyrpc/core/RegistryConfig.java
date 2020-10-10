package com.tiger.easyrpc.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("easyrpc.registry.redis")
public class RegistryConfig {
    public RegistryConfig(){
        EasyRpcManager.getInstance().setRegistryConfig(this);
    }
    private String host;
    private int port;
    private int timeout;
    private String password;
    private Map<String,String> pool;
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
}
