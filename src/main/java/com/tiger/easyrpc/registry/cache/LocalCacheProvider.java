package com.tiger.easyrpc.registry.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LocalCacheProvider extends AbstractCacheProvider {

    private Map<String, Object> cacheMap = new ConcurrentHashMap<String, Object>();

    public LocalCacheProvider(String cacheName) {
        super(cacheName);
    }

    @Override
    protected void putInternal(String key, Object obj) {
        cacheMap.put(key, obj);
    }

    @Override
    protected Object getInternal(String key) {
        return cacheMap.get(key);
    }

    @Override
    protected void removeInternal(String key) {
        cacheMap.remove(key);
    }

    @Override
    protected void clearInternal() {
        cacheMap.clear();
    }

    @Override
    protected void putAllInternal(Map<String, Object> allData) {
        Map<String, Object> cacheMapTemp = new ConcurrentHashMap<String, Object>(allData);
        cacheMap = cacheMapTemp;
    }

    @Override
    protected Map<String, Object> getAllInternal() {
        return cacheMap;
    }

    @Override
    protected void putInternal(Map<String, Object> objs) {
        Set<Map.Entry<String, Object>> entries = objs.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            cacheMap.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected Map<String, Object> getInternal(String[] keys) {
        Map<String, Object> reData = new HashMap<String, Object>(keys.length);
        for (String key : keys) {
            reData.put(key, cacheMap.get(key));
        }
        return reData;
    }

    @Override
    protected void removeInternal(String[] keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    @Override
    public Set<String> keys() {
        return new HashSet<String>(cacheMap.keySet());
    }
}
