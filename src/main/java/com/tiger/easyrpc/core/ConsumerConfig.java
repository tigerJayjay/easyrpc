package com.tiger.easyrpc.core;


import com.tiger.easyrpc.core.entity.Service;
import com.tiger.easyrpc.rpc.Parameter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("easyrpc.client")
public class ConsumerConfig implements Config {
    private String remoteUrl;
    private String interfacepath;
    private Service service;
    private Parameter parameter;

    public ConsumerConfig(){
        EasyRpcManager.getInstance().setConsumerConfig(this);
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
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

    public String getInterfacepath() {
        return interfacepath;
    }

    public void setInterfacepath(String interfacepath) {
        this.interfacepath = interfacepath;
    }


    public void close() {

    }

    public String getVersion(){
        return this.service.getVersion();
    }

    public String getGroup(){
        return this.service.getGroup();
    }


}
