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
     * 注册中心服务地址更新
     * @param key 服务名称:版本:分组
     * @param value 服务地址 ip:port
     * @param opr 操作类型 0：注册 1：下线
     */
    boolean putServiceUrl(String key,String value,int opr);

    /**
     * 移除服务
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

    /**
     * 投票+1
     */
    void vote(String key);

    /**
     * 发起投票，如果当前url被其他客户端发起投票，本次投票申请失败
     * @param url 服务url
     * @param channel 通道名称
     * @return 发起结果
     */
    boolean startVote(String channel,String url);

    String getVoteResult(String key);

}
