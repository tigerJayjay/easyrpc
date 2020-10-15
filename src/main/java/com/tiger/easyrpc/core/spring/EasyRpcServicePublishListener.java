package com.tiger.easyrpc.core.spring;


import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.registry.RegistryManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.tiger.easyrpc.common.EasyrpcConstant.COMMON_SYMBOL_MH;


/**
 * 当所有服务初始化完成可用之后初始化easyrpc相关组件
 */
public class EasyRpcServicePublishListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
        //注册关闭钩子
        registCloseHook();
        //暴露服务
        EasyRpcManager.getInstance().exportService();

    }

    private void registCloseHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        boolean server = EasyRpcManager.getInstance().isServer();
                        if(server){
                            InetAddress localHost = null;
                            try {
                                localHost = InetAddress.getLocalHost();
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                            String hostName = localHost.getHostAddress();
                            Integer port = EasyRpcManager.getInstance().getProviderConfig().getPort();
                            try {
                                //通知注册中心，下线当前服务
                                RegistryManager.getInstance().unregist(hostName+COMMON_SYMBOL_MH+port);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }
        ));
    }
}
