package com.tiger.easyrpc.registry.redis.jedis;

import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.config.RedisConfig;
import com.tiger.easyrpc.core.config.RegistryConfig;
import redis.clients.jedis.JedisPool;

/**
 * Jedis单实例操作客户端
 */
public class SingleRedisClient extends AbstractRedisClient {
    public SingleRedisClient(){
        RegistryConfig registryConfig = EasyRpcManager.getInstance().getRegistryConfig();
        RedisConfig redisConfig = registryConfig.getRedis();
        pool = new JedisPool(getPoolConfig(),redisConfig.getHost(),redisConfig.getPort(),
                redisConfig.getTimeout(),redisConfig.getPassword());
    }

}
