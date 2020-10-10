package com.tiger.easyrpc.core;


import com.tiger.easyrpc.core.entity.Service;
import com.tiger.easyrpc.rpc.Parameter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("easyrpc.client")
public class ConsumerConfig{
    private String remoteUrl;
    private String interfacepath;
    private Service service;
    private Parameter parameter;
    //远程调用超时时间 毫秒
    private Long rpcTimeout = 5000l;

    public Long getRpcTimeout() {
        return rpcTimeout;
    }

    public void setRpcTimeout(Long rpcTimeout) {
        this.rpcTimeout = rpcTimeout;
    }

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

    public String getVersion(){
        return this.service.getVersion();
    }

    public String getGroup(){
        return this.service.getGroup();
    }


}
