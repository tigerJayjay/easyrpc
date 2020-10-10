package com.tiger.easyrpc.remote;

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

public class ClientManager {
    private static ClientManager clientManager = new ClientManager();
    private static ConcurrentHashMap<String, Client> clientMap = new ConcurrentHashMap<String,Client>();

    private ClientManager(){
        init();
    }

    public void init(){
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(2);
        scheduledExecutorService.scheduleAtFixedRate(new ClientScanTask(),0, NO_CONNECT_CLIENT_SCAN_INTERVAL, TimeUnit.MILLISECONDS);
    }
    class ClientScanTask implements Runnable{
        @Override
        public void run() {
            for(Map.Entry<String, Client> entry: clientMap.entrySet()){
                Client value = entry.getValue();
                if(value instanceof NettyClient){
                    NettyClient var1 = (NettyClient)value;
                    if(var1.getStatus() == CLIENT_STATUS_DISCONNECT){
                        var1.retryConnect();
                    }
                    if(var1.getStatus() == CLIENT_STATUS_DIE){
                        try {
                            var1.close();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        clientMap.remove(entry.getKey());
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

    public Client addSynClient(String key,Client client){
       return clientMap.putIfAbsent(key,client);
    }
    public void removeClient(String key){
        clientMap.remove(key);
    }

    public Set<Map.Entry<String,Client>> getClients(){
       return Collections.unmodifiableSet(clientMap.entrySet());
    }

    public Client getClient(String key){
        return clientMap.get(key);
    }
}
