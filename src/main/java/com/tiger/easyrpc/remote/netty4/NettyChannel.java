package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.remote.api.Channel;
import java.util.concurrent.ConcurrentHashMap;


public class NettyChannel implements Channel {
    private String url;
    private static ConcurrentHashMap<String,NettyClient> clientMap = new ConcurrentHashMap<>();
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
    public void sendMessage(Object o) throws Exception {
        NettyClient client = clientMap.putIfAbsent(url, new NettyClient(url));
        if(client == null){
            client = clientMap.get(url);
            client.connect();
        }
        client.sendMessage(o);
    }

    @Override
    public void receiveMessage(Object o) {
        this.resultFuture.setResult(o);
        this.resultFuture.setAccept(true);
    }


    public class ResultFuture {
        private volatile boolean accept;
        private volatile Object result;

        public boolean isAccept() {
            return accept;
        }

        public void setAccept(boolean accept) {
            this.accept = accept;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }
    }

}
