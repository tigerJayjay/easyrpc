package com.tiger.easyrpc.core.config;

import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.entity.Service;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 服务端配置类
 */
@ConfigurationProperties("easyrpc.server")
public class ProviderConfig{
    private Service service;
    private Integer port;
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

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
