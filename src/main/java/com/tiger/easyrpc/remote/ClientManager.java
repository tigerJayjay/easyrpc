package com.tiger.easyrpc.remote;

import com.tiger.easyrpc.registry.RegistryManager;
import com.tiger.easyrpc.remote.api.Client;
import com.tiger.easyrpc.remote.netty4.NettyClient;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.tiger.easyrpc.common.EasyrpcConstant.*;

/**
 * 客户端管理器，用来定时剔除不可用客户端
 */
public class ClientManager {
    private static ClientManager clientManager = new ClientManager();
    private static ConcurrentHashMap<String, Client> clientMap = new ConcurrentHashMap<String,Client>();
    private final int VOTE_WAIT_TIME = 5000;
    private ClientManager(){
        init();
    }

    public void init(){
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleWithFixedDelay(new ClientScanTask(),0, NO_CONNECT_CLIENT_SCAN_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private String  getVoteResult(String url,RegistryManager registryManager){
        try {
            Thread.sleep(VOTE_WAIT_TIME);
            return registryManager.voteResult(url);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    class ClientScanTask implements Runnable{
        @Override
        public void run() {
            for(Map.Entry<String, Client> entry: clientMap.entrySet()){
                Client value = entry.getValue();
                if(value instanceof NettyClient){
                    NettyClient var1 = (NettyClient)value;
                    if(var1.getStatus() == CLIENT_STATUS_DISCONNECT){
                        //客户端与服务端断开连接，发起投票，确定是否剔除服务
                        RegistryManager registryManager = RegistryManager.getInstance();
                        String url = var1.getHost() + COMMON_SYMBOL_MH + var1.getPort();
                        registryManager.vote(url);
                        //发起投票成功
                        //获取结果
                        String s = getVoteResult(url,registryManager);
                        System.out.println("投票结果:"+s);
                        if(s != null && Integer.valueOf(s) > 1){
                            //重连3次
                            var1.retryConnect();
                        }else{
                            try {
                                //下线服务
                                registryManager.unregist(url);
                                //关闭连接
                                var1.close();
                                //移除客户端缓存
                                clientMap.remove(url);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        }
    }


    public static ClientManager getInstance(){
        return clientManager;
    }

    public void addClient(String key,Client client){
        clientMap.put(key,client);
    }

    /**
     * 添加异步客户端
     * @param key 客户端id
     * @param client 客户端对象
     * @return
     */
    public Client addSynClient(String key,Client client){
       return clientMap.putIfAbsent(key,client);
    }

    /**
     * 移除客户端
     * @param key
     */
    public void removeClient(String key){
        clientMap.remove(key);
    }

    /**
     * 获取不可修改客户端集合
     * @return
     */
    public Set<Map.Entry<String,Client>> getClients(){
       return Collections.unmodifiableSet(clientMap.entrySet());
    }

    public Client getClient(String key){
        return clientMap.get(key);
    }
}
