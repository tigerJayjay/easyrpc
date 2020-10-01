package com.tiger.easyrpc.core.metadata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetadataManager {
    private Map<Integer,AnnotationMetadata> metadataMap = new ConcurrentHashMap<Integer,AnnotationMetadata>();
    private static MetadataManager metadataManager = new MetadataManager();
    private MetadataManager(){}

    public static MetadataManager getInstance(){
        return metadataManager;
    }
    public AnnotationMetadata getMetadata(Integer sourcePath){
        return metadataMap.get(sourcePath);
    }

    public void setMetadata(AnnotationMetadata metadata){
        int sourcePath = System.identityHashCode(metadata.getSource());
        this.metadataMap.put(sourcePath,metadata);
    }
}
