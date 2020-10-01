package com.tiger.easyrpc.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("easyrpc.server")
public class ProviderConfig implements Config {
    private Integer port;

    public ProviderConfig(){
        EasyRpcManager.getInstance().setProviderConfig(this);
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void close() {

    }
}
