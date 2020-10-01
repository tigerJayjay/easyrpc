package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.remote.api.Channel;
import com.tiger.easyrpc.rpc.ResultFuture;

import java.util.HashMap;


public class NettyChannel implements Channel {
    private String url;
    private static HashMap<String,NettyClient> clientMap = new HashMap<>();
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
        synchronized (clientMap){
            NettyClient client = clientMap.get(url);
            if(client == null){
                client = new NettyClient(url);
                try {
                    client.connect();
                } catch (Exception e) {
                    throw new RuntimeException("远程服务连接失败！",e);
                }
                clientMap.put(url,client);
            }
            client.sendMessage(o);
        }
    }

    @Override
    public void receiveMessage(Object o) {
        this.resultFuture.setResult(o);
    }




}
