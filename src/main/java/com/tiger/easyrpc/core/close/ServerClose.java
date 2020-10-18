package com.tiger.easyrpc.core.close;

import com.tiger.easyrpc.core.Closable;
import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.registry.RegistryManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.tiger.easyrpc.common.EasyrpcConstant.COMMON_SYMBOL_MH;

public class ServerClose implements Closable {

    @Override
    public void close() {
        registCloseHook();
    }

    private void registCloseHook(){
        boolean server = EasyRpcManager.getInstance().isServer();
        if(server) {
            InetAddress localHost = null;
            try {
                localHost = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            String hostName = localHost.getHostAddress();
            Integer port = EasyRpcManager.getInstance().getProviderConfig().getPort();
            try {
                //通知注册中心，下线当前服务
                RegistryManager.getInstance().unregist(hostName + COMMON_SYMBOL_MH + port);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
