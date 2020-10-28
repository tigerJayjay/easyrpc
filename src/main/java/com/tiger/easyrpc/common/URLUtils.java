package com.tiger.easyrpc.common;

import com.tiger.easyrpc.core.EasyRpcManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.tiger.easyrpc.common.EasyrpcConstant.COMMON_SYMBOL_MH;

public class URLUtils {
    public static String getLocalUrl(){
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String hostName = localHost.getHostAddress();
        Integer port = EasyRpcManager.getInstance().getProviderConfig().getPort();
        return hostName + COMMON_SYMBOL_MH + port;
    }

}
