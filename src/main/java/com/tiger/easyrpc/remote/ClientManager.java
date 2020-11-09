package com.tiger.easyrpc.remote;

import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.registry.RegistryManager;
import com.tiger.easyrpc.remote.api.Client;
import com.tiger.easyrpc.remote.netty4.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static com.tiger.easyrpc.common.EasyrpcConstant.*;

/**
 * 客户端管理器，用来定时剔除不可用客户端
 */
public class ClientManager {
    private Logger logger = LoggerFactory.getLogger(ClientManager.class);
    private static ClientManager clientManager = new ClientManager();
    private static ConcurrentHashMap<String, Client> clientMap = new ConcurrentHashMap<String,Client>();
    private final int VOTE_WAIT_TIME = 5000;
    private ClientManager(){
        init();
    }

    public void init(){
        //开启了注册中心配置，才会定时对不可用服务进行投票剔除
        if(EasyRpcManager.getInstance().isEnableRegistry()){
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r,"vote-thread");
                    return t;
                }
            });
            long scanInterval = NO_CONNECT_CLIENT_SCAN_INTERVAL;
            Long configScanInterval = EasyRpcManager.getInstance().getRegistryConfig().getVoteInterval();
            if(configScanInterval != null){
                scanInterval = configScanInterval;
            }
            scheduledExecutorService.scheduleWithFixedDelay(new ClientScanTask(),0, scanInterval, TimeUnit.MILLISECONDS);
        }
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
            try{
                logger.info("投票检测...");
                Map<String,Client> disconnectUrlMap = new LinkedHashMap<>();
                RegistryManager registryManager = RegistryManager.getInstance();
                for(Map.Entry<String, Client> entry: clientMap.entrySet()){
                    Client value = entry.getValue();
                    if(value instanceof NettyClient){
                        NettyClient var1 = (NettyClient)value;
                        if(var1.getStatus() == CLIENT_STATUS_DISCONNECT){
                            //客户端与服务端断开连接，发起投票，确定是否剔除服务
                            String url = var1.getHost() + COMMON_SYMBOL_MH + var1.getPort();
                            registryManager.vote(url);
                            logger.info("投票完成，等待结果...");
                            disconnectUrlMap.put(url,value);
                        }
                    }
                }
                long voteWait = WAIT_VOTE_RESULT;
                Long configVoteWait = EasyRpcManager.getInstance().getRegistryConfig().getVoteWait();
                if(configVoteWait != null){
                    voteWait = configVoteWait;
                }
                Thread.sleep(voteWait);
                disconnectUrlMap.forEach((url,client)->{
                    //获取结果
                    String s = getVoteResult(url,registryManager);
                    logger.info("{}投票结果:{}",url,s);
                    //如果s为null，表示上一轮投票结果已经失效，需要由该客户端在下次重新发起投票
                    //在投票期间，客户端也在重连，由于在等待投票结果期间存在服务恢复的可能，所以需要再次验证一下客户端连接状态
                    if(s != null && Integer.valueOf(s) < 1
                        && client.getStatus() == CLIENT_STATUS_DISCONNECT){
                        //下线服务
                        try {
                            registryManager.unregist(url);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //通知服务下线
                        registryManager.publishUrlChange();
                        //关闭客户端
                        client.close();
                        //移除客户端缓存
                        clientMap.remove(url);
                    }
                });
            }catch (Exception e){
                logger.error("投票检测调度失败...",e);
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
