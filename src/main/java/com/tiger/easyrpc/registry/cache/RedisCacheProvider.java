package com.tiger.easyrpc.registry.cache;

import com.tiger.easyrpc.registry.redis.IRedisClient;

import java.util.Map;
import java.util.Set;

/**
 * redis缓存操作类
 */
public class RedisCacheProvider extends AbstractCacheProvider {
    private IRedisClient redisClient;

    public void setRedisClient(IRedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public RedisCacheProvider(String cacheName){super(cacheName);}
    @Override
    protected void putInternal(String key, Object obj) {

    }

    @Override
    protected void putInternal(Map<String, Object> objs) {

    }

    @Override
    protected Object getInternal(String key) {
        return null;
    }

    @Override
    protected Map<String, Object> getInternal(String[] keys) {
        return null;
    }

    @Override
    protected void removeInternal(String key) {

    }

    @Override
    protected void removeInternal(String[] keys) {

    }

    @Override
    protected void clearInternal() {

    }

    @Override
    protected void putAllInternal(Map<String, Object> allData) {

    }

    @Override
    protected Map<String, Object> getAllInternal() {
        return null;
    }

    @Override
    public Set<String> keys() {
        return null;
    }
}
