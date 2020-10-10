package com.tiger.easyrpc.registry.cache;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCacheProvider implements ICache {


    private String cacheName;


    public AbstractCacheProvider(String cacheName) {
        super();
        this.cacheName = cacheName;
    }

    @Override
    public String getCacheName() {
        return cacheName;
    }

    @Override
    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }
    private  boolean isEmpty(Object[] array) {
        return ArrayUtils.isEmpty(array);
    }

    private  boolean isEmpty(Collection<?> collection) {
        return CollectionUtils.isEmpty(collection);
    }
    private  boolean isEmpty(Map<?, ?> map) {
        return CollectionUtils.isEmpty(map);
    }
    @Override
    public void put(String key, Object obj) {
        if (StringUtils.isBlank(key) || obj == null) {
            return;
        }
        putInternal(key, obj);
    }

    protected abstract void putInternal(String key, Object obj);

    @Override
    public void put(Map<String, Object> objs) {
        if (isEmpty(objs)) {
            return;
        }
        // 清除空数据
        Iterator<Map.Entry<String, Object>> it = objs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if (StringUtils.isBlank(entry.getKey()) || entry.getValue() == null) {
                it.remove();
            }
        }
        putInternal(objs);
    }

    protected abstract void putInternal(Map<String, Object> objs);

    @Override
    public Object get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Object value = getInternal(key);
        return value;
    }

    protected abstract Object getInternal(String key);

    @Override
    public Map<String, Object> get(String[] keys) {
        if (isEmpty(keys)) {
            return Collections.emptyMap();
        }
        Map<String, Object> values = getInternal(keys);
        return values;
    }

    protected abstract Map<String, Object> getInternal(String[] keys);

    @Override
    public void remove(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        removeInternal(key);
    }

    protected abstract void removeInternal(String key);

    @Override
    public void remove(String[] keys) {
        if (isEmpty(keys)) {
            return;
        }
        removeInternal(keys);
    }

    protected abstract void removeInternal(String[] keys);

    @Override
    public void clear() {
        clearInternal();
    }

    protected abstract void clearInternal();

    @Override
    public void putAll(Map<String, Object> allData) {
        if (isEmpty(allData)) {
            return;
        }
        // 清除空数据
        Iterator<Map.Entry<String, Object>> it = allData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if (StringUtils.isBlank(entry.getKey()) || entry.getValue() == null) {
                it.remove();
            }
        }
        putAllInternal(allData);
    }

    protected abstract void putAllInternal(Map<String, Object> allData);

    @Override
    public Map<String, Object> getAll() {
        Map<String, Object> values = getAllInternal();
        values = new ConcurrentHashMap<String, Object>(values);
        return values;
    }

    protected abstract Map<String, Object> getAllInternal();

}

