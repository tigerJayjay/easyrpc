package com.tiger.easyrpc.core.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionWrapper {
    private Object defaultExtension;
    private Map<String,Object> extensionWrappers = new ConcurrentHashMap<String,Object>();

    public void addExtension(Object o){

    }

    public void setDefaultExtension(Object defaultExtension){
        this.defaultExtension = defaultExtension;
    }

    public Object getDefaultExtension(){
        return this.defaultExtension;
    }

}
