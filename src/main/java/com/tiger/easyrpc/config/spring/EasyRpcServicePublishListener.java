package com.tiger.easyrpc.config.spring;


import com.tiger.easyrpc.config.EasyRpcManager;
import com.tiger.easyrpc.remote.netty4.NettyClient;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 当所有服务初始化完成可用之后，开始暴露服务接口
 */
@Component
public class EasyRpcServicePublishListener implements ApplicationListener {
    private ExecutorService service = Executors.newFixedThreadPool(2);
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if(applicationEvent instanceof ContextRefreshedEvent){
            service.submit(new Runnable() {
                @Override
                public void run() {
                    EasyRpcManager.getInstance().exportService();
                }
            });
            NettyClient client = new NettyClient();
            try {
                client.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
