package com.tiger.easyrpc.core.entity;

/**
 * 服务信息配置类
 */
public class Service {

    private String scan;
    private String version;
    private String group;

    public String getScan() {
        return scan;
    }

    public void setScan(String scan) {
        this.scan = scan;
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
