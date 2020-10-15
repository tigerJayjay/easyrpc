package com.tiger.easyrpc.core.cache.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 服务端扫描服务对象管理器
 */
public class ExportServiceManager {
    //保存暴露服务对象
    public static Map<String,Class> services = new HashMap<String,Class>(156);
    //保存暴露服务Class信息
    public static Set<Class> exporterClass = new HashSet<Class>(256);

    private ExportServiceManager(){};

}
