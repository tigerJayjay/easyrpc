package com.tiger.easyrpc.core.metadata;

public abstract class AnnotationMetadata {
    private Object source;

    public AnnotationMetadata() {
    }

    public AnnotationMetadata(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }
}

