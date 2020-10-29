package com.tiger.easyrpc.common;

import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.ProviderConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.tiger.easyrpc.common.EasyrpcConstant.COMMON_SYMBOL_MH;

public class URLUtils {
    public static String getLocalServerUrlAndPort(){
        String hostName = getLocalUrl();
        EasyRpcManager easyRpcManager = EasyRpcManager.getInstance();
        ProviderConfig providerConfig = easyRpcManager.getProviderConfig();
        Integer port = providerConfig.getPort();
        return hostName + COMMON_SYMBOL_MH + port;
    }

    public static String getLocalUrl(){
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return localHost.getHostAddress();
    }

}
