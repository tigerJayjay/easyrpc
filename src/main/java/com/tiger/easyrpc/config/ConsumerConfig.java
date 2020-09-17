package com.tiger.easyrpc.config;


import com.tiger.easyrpc.rpc.api.Parameter;

public class ConsumerConfig implements Config {
    private String[] remoteUrl;
    private Parameter parameter;

    public String[] getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String[] remoteUrl) {
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
