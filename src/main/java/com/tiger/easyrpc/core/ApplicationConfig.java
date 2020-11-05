package com.tiger.easyrpc.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 全局配置
 */
@ConfigurationProperties("easyrpc")
public class ApplicationConfig {

    public ApplicationConfig(){
        EasyRpcManager.getInstance().setApplicationConfig(this);
    }
    private boolean auto;
    private String netHost;
    private String netHostPre;


    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public String getNetHost() {
        return netHost;
    }

    public void setNetHost(String netHost) {
        this.netHost = netHost;
    }

    public String getNetHostPre() {
        return netHostPre;
    }

    public void setNetHostPre(String netHostPre) {
        this.netHostPre = netHostPre;
    }
}
