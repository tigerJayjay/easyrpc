package com.tiger.easyrpc.core;


import com.tiger.easyrpc.rpc.api.Parameter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("easyrpc.client")
public class ConsumerConfig implements Config {
    private String remoteUrl;
    private Parameter parameter;

    public ConsumerConfig(){
        EasyRpcManager.getInstance().setConsumerConfig(this);
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    public void close() {

    }
}
