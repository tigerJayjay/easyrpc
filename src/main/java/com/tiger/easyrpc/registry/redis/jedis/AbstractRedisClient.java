package com.tiger.easyrpc.registry.redis.jedis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.config.RegistryConfig;
import com.tiger.easyrpc.registry.redis.IRedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.util.Pool;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Jedis客户端抽象类，用来统一配置redis参数
 */
public abstract class AbstractRedisClient implements IRedisClient {
    protected static final String REGISTRY_LOCK = "registry_lock";
    protected static final String LOCK_SUCCESS = "OK";
    protected static final String UNLOCK_SUCCESS = "1";
    protected long expireTime = 30000;//锁过期时间
    protected Pool<Jedis> pool;
    private JedisPoolConfig poolConfig;
    public AbstractRedisClient(){
        ObjectMapper mapper = new ObjectMapper();
        RegistryConfig registryConfig = EasyRpcManager.getInstance().getRegistryConfig();
        if(registryConfig == null){
            throw new RuntimeException("未配置注册中心参数！");
        }
        poolConfig = mapper.convertValue(registryConfig.getRedis().getPool(),JedisPoolConfig.class);
        if (poolConfig == null){
            poolConfig = new JedisPoolConfig();
        }
    }

    public JedisPoolConfig getPoolConfig() {
        return poolConfig;
    }

    @Override
    public String hget(String key, String field) {
        Jedis resource = pool.getResource();
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
        Jedis resource = pool.getResource();
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
        Jedis resource = pool.getResource();
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
        Jedis resource = pool.getResource();
        try{
            resource.subscribe(jedisPubSub,channels);
        }finally {
            resource.close();
        }
    }

    @Override
    public Long publish(String channel, String message) {
        Jedis resource = pool.getResource();
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
        Jedis resource = pool.getResource();
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
        Jedis resource = pool.getResource();
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
        Jedis resource = pool.getResource();
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
        Jedis resource = pool.getResource();
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
        Jedis resource = pool.getResource();
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
        Jedis resource = pool.getResource();
        try{
            resource.del(key);
        }finally {
            resource.close();
        }
    }

    @Override
    public boolean lock(String requestId) {

        Jedis resource = pool.getResource();
        try{
            //SET命令的参数
            SetParams params = SetParams.setParams().nx().px(expireTime);
            String result = resource.set(REGISTRY_LOCK, requestId,params);
            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
        }finally {
            resource.close();
        }
        return false;
    }

    @Override
    public boolean unlock(String requestId) {
        Jedis resource = pool.getResource();
        try{
            String script =
                    "if redis.call('get',KEYS[1]) == ARGV[1] then" +
                            "   return redis.call('del',KEYS[1]) " +
                            "else" +
                            "   return 0 " +
                            "end";
            Object result = resource.eval(script, Collections.singletonList(REGISTRY_LOCK),
                    Collections.singletonList(requestId));
            if(UNLOCK_SUCCESS.equals(result.toString())){
                return true;
            }
        }finally {
            resource.close();
        }

        return false;
    }


}
