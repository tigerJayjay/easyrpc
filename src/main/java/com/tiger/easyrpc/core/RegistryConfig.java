package com.tiger.easyrpc.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 注册中心配置类
 */
@ConfigurationProperties("easyrpc.registry.redis")
public class RegistryConfig {
    public RegistryConfig(){
        EasyRpcManager.getInstance().setRegistryConfig(this);
    }
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
