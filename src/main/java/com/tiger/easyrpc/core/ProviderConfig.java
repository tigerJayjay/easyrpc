package com.tiger.easyrpc.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("easyrpc.server")
public class ProviderConfig implements Config {
    private int port;

    public ProviderConfig(){
        EasyRpcManager.getInstance().setProviderConfig(this);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void close() {

    }
}
