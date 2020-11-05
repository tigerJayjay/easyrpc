package com.tiger.easyrpc.common;

import com.tiger.easyrpc.core.ApplicationConfig;
import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.ProviderConfig;
import org.apache.commons.lang3.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static com.tiger.easyrpc.common.EasyrpcConstant.COMMON_SYMBOL_MH;

public class URLUtils {
    /**
     * 如果配置了nethost配置，则返回该ip，否则返回getLocalUrl()
     * @return
     */
    public static String getLocalServerUrlAndPort(){

        EasyRpcManager easyRpcManager = EasyRpcManager.getInstance();
        ApplicationConfig applicationConfig = easyRpcManager.getApplicationConfig();
        ProviderConfig providerConfig = easyRpcManager.getProviderConfig();
        String prefix = applicationConfig.getNetHostPre();
        Integer port = providerConfig.getPort();
        String hostName = null;
        if(!StringUtils.isEmpty(applicationConfig.getNetHost())){
            hostName = applicationConfig.getNetHost();
        }else{
            hostName = getLocalUrl(prefix);
        }
        return hostName + COMMON_SYMBOL_MH + port;
    }

    /**
     * 如果本机地址包含内网地址，并且符合prefix过滤，则返回该ip
     * 否则返回siteLocal地址，如果上面两种ip都没有，则返回loopback地址
     * @param prefix ip前缀
     * @return
     */
    public static String getLocalUrl(String... prefix){
        String loopbackAddress = null;
        String indicateAddress = null;
        Enumeration<NetworkInterface> en = null;//获取本地所有网络接口
        try {
            en = NetworkInterface.getNetworkInterfaces();//获取本地所有网络接口
            while (en.hasMoreElements()) {//遍历枚举中的每一个元素
                NetworkInterface ni= en.nextElement();
                Enumeration<InetAddress> enumInetAddr = ni.getInetAddresses();
                while (enumInetAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddr.nextElement();
                    if(!(inetAddress instanceof Inet4Address)){
                        continue;
                    }
                    if(!inetAddress.isLoopbackAddress()){
                        if(inetAddress.isSiteLocalAddress()){
                            String hostAddress = inetAddress.getHostAddress();
                            if(prefix != null && prefix.length >0 &&
                                    !StringUtils.isEmpty(prefix[0])&&
                                    !hostAddress.startsWith(prefix[0])){
                                continue;
                            }
                            return hostAddress;
                        }
                        if(inetAddress.isLinkLocalAddress()){
                            indicateAddress = inetAddress.getHostAddress();
                        }
                    }else{
                        loopbackAddress = inetAddress.getHostAddress();
                    }
                }
            }
            return indicateAddress == null ? loopbackAddress : indicateAddress;
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

}
