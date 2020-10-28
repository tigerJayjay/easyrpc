package com.tiger.easyrpc.core.close;

import com.tiger.easyrpc.common.URLUtils;
import com.tiger.easyrpc.core.Closable;
import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.registry.RegistryManager;

public class ServerClose implements Closable {

    @Override
    public void close() {
        registCloseHook();
    }

    private void registCloseHook(){
        boolean server = EasyRpcManager.getInstance().isServer();
        if(server) {
            try {
                //通知注册中心，下线当前服务
                RegistryManager.getInstance().unregist(URLUtils.getLocalUrl());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
