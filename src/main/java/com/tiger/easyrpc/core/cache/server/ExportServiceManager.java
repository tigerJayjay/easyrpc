package com.tiger.easyrpc.core.cache.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExportServiceManager {
    public static Map<String,Class> services = new HashMap<String,Class>(156);
    public static Set<Class> exporterClass = new HashSet<Class>(256);

    private ExportServiceManager(){};

}
