package com.tiger.easyrpc.core.spring;


import com.tiger.easyrpc.core.EasyRpcManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * 当所有服务初始化完成可用之后初始化easyrpc相关组件
 */
public class EasyRpcServicePublishListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
        AbstractApplicationContext context = (AbstractApplicationContext)applicationEvent.getApplicationContext();
        context.registerShutdownHook();
        //暴露服务
        EasyRpcManager.getInstance().exportService();
    }
}
