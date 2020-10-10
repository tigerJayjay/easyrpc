package com.tiger.easyrpc.registry;

import redis.clients.jedis.JedisPubSub;

import java.util.Map;

/**
 * 注册中心操作接口
 */
public interface IRegistry {
    /**
     * 获取注册中心服务地址
     * @return
     */
    Map<String, String> getServiceUrlList();

    /**
     * 新增注册中心服务地址
     * @param key
     * @param value
     */
    boolean putServiceUrl(String key,String value);

    /**
     * 删除注册中心服务地址
     * @param key
     * @return
     */
    boolean delServiceUrl(String key);

    /**
     * 监听注册中心变化情况
     * @param jedisPubSub
     */
    void subscribe(JedisPubSub jedisPubSub,String channel);

    /**
     * 发布注册中心变动事件
     */
    void publish(String channel,String mes);
}
