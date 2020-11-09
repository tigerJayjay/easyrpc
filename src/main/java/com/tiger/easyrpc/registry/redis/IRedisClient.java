package com.tiger.easyrpc.registry.redis;

import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.params.SetParams;

import java.util.List;
import java.util.Map;

/**
 * redis客户端接口，用于获取或添加url信息,以及订阅url或发布url改动信息
 */
public interface IRedisClient {
    String hget(String key,String field);

    Long hset(String key,String field,String value);

    Map<String, String> hgetAll(String key);

    void subscribe(JedisPubSub jedisPubSub, String... channels);

    Long publish(String channel, String message);

    String set(String key, String value, SetParams params);

    Object eval(String script, List<String> keys, List<String> args);

    Long hdel(String key,String field);

    Long incr(String key);

    String get(String key);

    void del(String key);

    /**
     * redis分布式加锁
     * @param requestId  解锁时用于区分该锁是否为该线程加的锁
     * @return
     */
    boolean lock(String requestId);

    /**
     * redis分布式解锁
     * @param requestId
     * @return
     */
    boolean unlock(String requestId);
}
