package com.tiger.easyrpc.core.cache.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExportServiceManager {
    public static Map<String,Object> services = new HashMap<String,Object>(156);
    public static Set<Class> exporterClass = new HashSet<Class>(256);

    private ExportServiceManager(){};

}
