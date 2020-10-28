package com.tiger.easyrpc.registry.redis.jedis;

import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.RegistryConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.params.SetParams;

import java.util.List;
import java.util.Map;

/**
 * Jedis单实例操作客户端
 */
public class SingleRedisClient extends AbstractRedisClient {
    private JedisPool jedisPool;
    public SingleRedisClient(){
        RegistryConfig registryConfig = EasyRpcManager.getInstance().getRegistryConfig();
        jedisPool = new JedisPool(getPoolConfig(),registryConfig.getHost(),registryConfig.getPort(),
                registryConfig.getTimeout(),registryConfig.getPassword());
    }

    @Override
    public String hget(String key, String field) {
        Jedis resource = jedisPool.getResource();
        String result = null;
        try{
            result = resource.hget(key, field);
        }finally {
            resource.close();
        }

        return result;
    }

    @Override
    public Long hset(String key, String field, String value) {
        Jedis resource = jedisPool.getResource();
        Long result = null;
        try{
            result = resource.hset(key, field, value);
        }finally {
            resource.close();
        }
        return result;
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        Jedis resource = jedisPool.getResource();
        Map<String, String> result = null;
        try{
            result =  resource.hgetAll(key);
        }finally {
            resource.close();
        }
        return result;
    }

    @Override
    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        Jedis resource = jedisPool.getResource();
        try{
            resource.subscribe(jedisPubSub,channels);
        }finally {
            resource.close();
        }
    }

    @Override
    public Long publish(String channel, String message) {
        Jedis resource = jedisPool.getResource();
        Long result = null;
        try{
            result = resource.publish(channel, message);
        }finally {
            resource.close();
        }
        return result;
    }

    @Override
    public String set(String key, String value, SetParams params) {
        Jedis resource = jedisPool.getResource();
        String result = null;
        try{
           result =  resource.set(key,value,params);
        }finally {
            resource.close();
        }
        return result;
    }

    @Override
    public Object eval(String script, List<String> keys, List<String> args) {
        Jedis resource = jedisPool.getResource();
        Object result = null;
        try{
            result =  resource.eval(script,keys,args);
        }finally {
            resource.close();
        }
        return result;
    }

    @Override
    public Long hdel(String key, String field) {
        Jedis resource = jedisPool.getResource();
        Long result = null;
        try{
            result =  resource.hdel(key,field);
        }finally {
            resource.close();
        }
        return result;
    }

    @Override
    public Long incr(String key) {
        Jedis resource = jedisPool.getResource();
        Long result = null;
        try{
            result =  resource.incr(key);
        }finally {
            resource.close();
        }
        return result;
    }

    @Override
    public String get(String key) {
        Jedis resource = jedisPool.getResource();
        String result = null;
        try{
            result =  resource.get(key);
        }finally {
            resource.close();
        }
        return result;
    }

    @Override
    public void del(String key) {
        Jedis resource = jedisPool.getResource();
        try{
           resource.del(key);
        }finally {
            resource.close();
        }
    }


}
