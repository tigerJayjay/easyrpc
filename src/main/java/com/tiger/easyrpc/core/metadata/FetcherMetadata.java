package com.tiger.easyrpc.core.metadata;
/**
 * Fetcher注解元数据信息
 */
public class FetcherMetadata extends AnnotationMetadata{
    private String url;
    private String version;
    private String group;

    public FetcherMetadata(){}
    public FetcherMetadata(String url, String version, String group,Object source) {
        super(source);
        this.url = url;
        this.version = version;
        this.group = group;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

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
