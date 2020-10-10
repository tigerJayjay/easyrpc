package com.tiger.easyrpc.registry.cache;

import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {
    private final ConcurrentHashMap<String, ICache> caches    = new ConcurrentHashMap<String, ICache>();
    private static final CacheManager instance = new CacheManager();
    private String sync = "";
    private CacheManager() {
    }

    public static CacheManager instance() {
        return instance;
    }

    public void initCache(String cacheName, String cacheProviderType) {
        ICache cache = getCacheProvider(cacheName, cacheProviderType);
    }

    public ICache getCacheProvider(String cacheName) {
        return caches.get(cacheName);
    }

    public ICache getCacheProvider(String cacheName, String cacheProviderType) {
        ICache cache = caches.get(cacheName);
        if (cache == null) {
            synchronized (sync) {
                cache = caches.get(cacheName);
                if (cache != null) {
                    return cache;
                }
                cache = initCacheInternal(cacheName, cacheProviderType);
            }
        }
        return cache;
    }

    public void addCacheProvider(ICache iCache) {
        caches.putIfAbsent(iCache.getCacheName(), iCache);
    }


    private ICache initCacheInternal(String cacheName, String cacheProviderType) {
        ICache cache = null;
        if(cacheProviderType.equals(CacheTypeEnum.Local.getType())){
            cache = new LocalCacheProvider(cacheName);
        }else if(cacheProviderType.equals(CacheTypeEnum.Redis.getType())){
            cache = new RedisCacheProvider(cacheName);
        }
        caches.put(cacheName, cache);
        return cache;
    }
}
