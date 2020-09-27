package com.tiger.easyrpc.core;

import com.tiger.easyrpc.remote.netty4.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 管理easyrpc的各个组件
 */
public class EasyRpcManager {
    private String serviceScanPath;
    private String easyrpcRootPath="com.tiger.easyrpc";
    private Logger logger = LoggerFactory.getLogger(EasyRpcManager.class);
    private static EasyRpcManager manager = new EasyRpcManager();
    private EasyRpcManager(){}
    private NettyServer nettyServer;
    private ProviderConfig providerConfig;
    private ConsumerConfig consumerConfig;
    private ConfigCenter configCenter;

    public static EasyRpcManager getInstance(){
        return manager;
    }

    public String getServiceScanPath() {
        return serviceScanPath;
    }

    public void setServiceScanPath(String serviceScanPath) {
        this.serviceScanPath = serviceScanPath;
    }

    public String getEasyrpcRootPath() {
        return easyrpcRootPath;
    }

    public void setEasyrpcRootPath(String easyrpcRootPath) {
        this.easyrpcRootPath = easyrpcRootPath;
    }

    public void setProviderConfig(ProviderConfig providerConfig){
        this.providerConfig = providerConfig;
    }

    public void setConsumerConfig(ConsumerConfig consumerConfig){
        this.consumerConfig = consumerConfig;
    }

    public void exportService(){
        if(providerConfig == null){
            logger.info("{}未设置！","providerConfig");
        }
        if(nettyServer == null){
            nettyServer = new NettyServer(this.providerConfig.getPort());
        }
        try {
            nettyServer.run();
        } catch (Exception e) {
            logger.error("暴露服务发生异常！",e);
        }
    }

    public String getClientRemoteUrl(){
        return this.consumerConfig.getRemoteUrl();
    }


}
