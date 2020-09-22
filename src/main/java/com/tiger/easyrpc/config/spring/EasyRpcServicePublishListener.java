package com.tiger.easyrpc.config.spring;


import com.tiger.easyrpc.config.EasyRpcManager;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 当所有服务初始化完成可用之后，开始暴露服务接口
 */
@Component
public class EasyRpcServicePublishListener implements ApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if(applicationEvent instanceof ContextRefreshedEvent){
            EasyRpcManager.getInstance().exportService();
        }
    }
}
