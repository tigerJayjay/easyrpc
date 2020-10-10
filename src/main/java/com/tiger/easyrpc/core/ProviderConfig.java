package com.tiger.easyrpc.core;

import com.tiger.easyrpc.core.entity.Service;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("easyrpc.server")
public class ProviderConfig{
    private Service service;
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

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getVersion() {
        return this.service.getVersion();
    }

    public String getGroup() {
        return this.service.getGroup();
    }
}
