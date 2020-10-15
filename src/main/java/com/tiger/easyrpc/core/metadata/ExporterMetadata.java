package com.tiger.easyrpc.core.metadata;

/**
 * Exporter注解元数据信息
 */
public class ExporterMetadata extends AnnotationMetadata {
    private String version;
    private String group;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
