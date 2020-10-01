package com.tiger.easyrpc.core.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionLoader {
    private Map<String,ExtensionWrapper> extensionWrappers = new ConcurrentHashMap<String,ExtensionWrapper>();


    public ExtensionWrapper getExtensionWrapper(String extensionTypeName){
        return extensionWrappers.get(extensionTypeName);
    }

    public void addExtensionWrapper(String extensionName,ExtensionWrapper extensionWrapper){
        this.extensionWrappers.put(extensionName,extensionWrapper);
    }
}
