package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.remote.ClientManager;
import com.tiger.easyrpc.remote.api.Channel;
import com.tiger.easyrpc.rpc.ResultFuture;


/**
 * Netty数据通道实现类
 */
public class NettyChannel implements Channel {
    private static String lock = "lock";
    private String url;
    private ResultFuture resultFuture;
    public NettyChannel(String url){
        this.url = url;
        this.resultFuture = new ResultFuture();
    }
    public String getUrl(){
        return this.url;
    }

    public ResultFuture getResultFuture(){
        return this.resultFuture;
    }

    @Override
    public void sendMessage(Object o){
        synchronized (lock){
            ClientManager instance = ClientManager.getInstance();
            NettyClient client =  new NettyClient(url);
            NettyClient client1 = (NettyClient)instance.addSynClient(url, client);
            if(client1 == null){
                try {
                    client.connect();
                } catch (Exception e) {
                    throw new RuntimeException("远程服务连接失败！",e);
                }
                client.sendMessage(o);
                return;
            }
            client = null;
            client1.sendMessage(o);
        }
    }

    @Override
    public void receiveMessage(Object o) {
        this.resultFuture.setResult(o);
    }

}
