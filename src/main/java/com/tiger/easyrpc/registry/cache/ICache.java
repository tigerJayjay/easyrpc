package com.tiger.easyrpc.registry.cache;

import java.util.Map;
import java.util.Set;

/**
 * 缓存接口，主要为本地和redis缓存两种实现
 */
public interface ICache {

    public String getCacheName();

    public void setCacheName(String cacheName);

    /**
     * 设置缓存key及value
     *
     * @param key
     * @param obj
     */
    public void put(String key, Object obj);

    /**
     * 设置多个key及value
     *
     * @param objs
     */
    public void put(Map<String, Object> objs);

    /**
     * 通过key获取值
     *
     * @param key
     * @return
     */
    public Object get(String key);

    /**
     * 获取多个key的value
     *
     * @param keys
     * @return
     */
    public Map<String, Object> get(String[] keys);

    /**
     * 删除key
     *
     * @param key
     */
    public void remove(String key);

    /**
     * 删除多个key
     *
     * @param keys
     */
    public void remove(String[] keys);

    /**
     * 清除所所有key及数据
     */
    public void clear();

    /**
     * 增加所有数据，以最新的缓存数据为准
     *
     * @param allData
     */
    public void putAll(Map<String, Object> allData);

    /**
     * 获取所有缓存数据
     *
     * @return
     */
    public Map<String, Object> getAll();

    /**
     * 返回缓存中所有KEY的列表
     *
     * @return
     */
    public Set<String> keys();
}
