package com.tiger.easyrpc.config.cache.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExportServiceManager {
    private static Map<String,Object> services = new ConcurrentHashMap<String,Object>();

    private ExportServiceManager(){};

    public static void setService(String key,Object service){
        services.put(key,service);
    }

    public static Object getService(String key){
        return services.get(key);
    }
}
