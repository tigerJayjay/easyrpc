package com.tiger.easyrpc.registry.redis.jedis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.RegistryConfig;
import com.tiger.easyrpc.registry.redis.IRedisClient;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Jedis客户端抽象类，用来统一配置redis参数
 */
public abstract class AbstractRedisClient implements IRedisClient {
    private JedisPoolConfig poolConfig;
    public AbstractRedisClient(){
        ObjectMapper mapper = new ObjectMapper();
        RegistryConfig registryConfig = EasyRpcManager.getInstance().getRegistryConfig();
        if(registryConfig == null){
            throw new RuntimeException("未配置注册中心参数！");
        }
        poolConfig = mapper.convertValue(registryConfig.getPool(),JedisPoolConfig.class);
        if (poolConfig == null){
            poolConfig = new JedisPoolConfig();
        }
    }

    public JedisPoolConfig getPoolConfig() {
        return poolConfig;
    }
}
