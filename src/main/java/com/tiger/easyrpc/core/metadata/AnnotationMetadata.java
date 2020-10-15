package com.tiger.easyrpc.core.metadata;

/**
 * 保存注解信息
 * source： 被注解的源对象信息
 * @see ExporterMetadata
 * @see FetcherMetadata
 */
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

