package com.tiger.easyrpc.core.cache.client;

import java.util.HashMap;
import java.util.Map;

/**
 * 保存使用fetcher注解的接口配置的服务地址
 */
public class FetcherServiceManager {
    public static Map<String,String> urlCache = new HashMap<String,String>();

}
