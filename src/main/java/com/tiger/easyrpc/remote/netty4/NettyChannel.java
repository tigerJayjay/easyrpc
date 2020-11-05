package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.remote.ClientManager;
import com.tiger.easyrpc.remote.api.Channel;
import com.tiger.easyrpc.remote.api.Client;
import com.tiger.easyrpc.rpc.ResultFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Netty数据通道实现类
 */
public class NettyChannel implements Channel {
    private static Logger logger = LoggerFactory.getLogger(NettyChannel.class);
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
        ClientManager instance = ClientManager.getInstance();
        Client var1 = instance.getClient(url);
        if(var1 != null){
           NettyClient var2  =(NettyClient)var1;
            var2.sendMessage(o);
            return;
        }
        NettyClient client =  new NettyClient(url);
        NettyClient pre = (NettyClient)instance.addSynClient(url, client);
        if(pre == null){
            logger.trace("{}的client为null，创建了{}",url,client);
            client.connect();
            client.sendMessage(o);
            return;
        }
        pre.sendMessage(o);
    }

    @Override
    public void receiveMessage(Object o) {
        this.resultFuture.setResult(o);
    }

}
