package com.tiger.easyrpc.core.metadata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetadataManager {
    private Map<Object,AnnotationMetadata> metadataMap = new ConcurrentHashMap<Object,AnnotationMetadata>();
    private static MetadataManager metadataManager = new MetadataManager();
    private MetadataManager(){}

    public static MetadataManager getInstance(){
        return metadataManager;
    }
    public AnnotationMetadata getMetadata(Object source){
        return metadataMap.get(source);
    }

    public void setMetadata(AnnotationMetadata metadata){
        this.metadataMap.put(metadata.getSource(),metadata);
    }
}
