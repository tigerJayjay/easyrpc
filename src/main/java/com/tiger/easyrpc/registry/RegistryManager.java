package com.tiger.easyrpc.registry;

import com.tiger.easyrpc.common.SysCacheEnum;
import com.tiger.easyrpc.common.URLUtils;
import com.tiger.easyrpc.registry.cache.CacheManager;
import com.tiger.easyrpc.registry.cache.CacheTypeEnum;
import com.tiger.easyrpc.registry.cache.ICache;
import com.tiger.easyrpc.registry.redis.RedisRegistry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.tiger.easyrpc.common.EasyrpcConstant.*;

/**
 * 注册中心管理器，用于服务可用性检测，服务发现，服务注册
 */
public class RegistryManager {
    private Logger logger = LoggerFactory.getLogger(RegistryManager.class);
    private static RegistryManager registryManager = new RegistryManager();
    private static ScheduledExecutorService scheculeService = Executors.newScheduledThreadPool(1);
    private static ExecutorService subService = Executors.newFixedThreadPool(2);
    private final String URL_CHANNEL = "urlChannel";
    private final String URL_CHANGE = "urlChange";
    private final String VOTE_CHANNEL = "voteChannel";
    private IRegistry registry;
    private boolean isInit;

    private RegistryManager(){

    }

    public synchronized void flushLocalCache(){
        logger.info("刷新本地缓存！");
        check();
        Map serviceUrlList = registry.getServiceUrlList();
        //放入本地缓存
        CacheManager.instance().initCache(SysCacheEnum.serviceurl.getCacheName(), CacheTypeEnum.Local.getType());
        ICache cacheProvider = CacheManager.instance().getCacheProvider(SysCacheEnum.serviceurl.getCacheName());
        cacheProvider.putAll(serviceUrlList);
    }

    public static RegistryManager getInstance(){
        return registryManager;
    }
    private void check(){
        if(!isInit){
            init();
        }
        if(this.registry == null){
            throw new RuntimeException("RegistryManager缺失Registry对象！");
        }
    }

    /**
     * 服务注册
     * @param service 服务名:版本:分组
     * @param url host:port
     * @throws InterruptedException
     */
    public void regist(String service,String url) throws InterruptedException {
        check();
        boolean b = this.registry.putServiceUrl(service, url,OPR_REGIST);
        while(!b){
            Thread.sleep(500);
            b = this.registry.putServiceUrl(service, url,OPR_REGIST);
        }
    }

    /**
     * 服务下线
     * @param url 服务地址 ip:port
     */
    public void unregist(String url) throws InterruptedException{
        check();
        boolean b = this.registry.putServiceUrl(null, url,OPR_UNREGIST);
        while(!b){
            Thread.sleep(500);
            b = this.registry.putServiceUrl(null, url,OPR_UNREGIST);
        }
    }

    public void publishUrlChange(){
        check();
        this.registry.publish(URL_CHANNEL,URL_CHANGE);
        logger.info("url变动事件发布");
    }

    /**
     * 服务端不可用投票
     * @param url 服务端地址
     */
    public boolean vote(String url){
        logger.info("客户端对连接失败服务发起移除投票！");
        check();
        return this.registry.startVote(VOTE_CHANNEL, url);
    }

    /**
     * 获取投票结果
     * @param url 服务端地址
     * @return
     */
    public String voteResult(String url){
        check();
        return this.registry.getVoteResult(url);
    }

    /**
     * 注册中心管理器初始化
     */
    public void init(){
        this.isInit = true;
        this.registry = new RedisRegistry();
        //初始化定时器，每30秒对服务进行存活检测，移除不可用服务
        //scheculeService.scheduleAtFixedRate(new UrlCheckTask(),0,30, TimeUnit.SECONDS);
        //设置服务变更监听
        subService.execute(new Runnable() {
            @Override
            public void run() {
                registry.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        logger.info("url变动事件消费");
                        flushLocalCache();
                    }
                },URL_CHANNEL);
            }
        });
        subService.execute(new Runnable() {
            @Override
            public void run() {
                registry.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        if(StringUtils.isEmpty(message)){
                            return;
                        }
                        String url = message;
                        String localUrl = URLUtils.getLocalUrl();
                        //对本机地址发起的投票，本机不参与投票
                        if(localUrl.equals(url)){
                            return;
                        }
                        boolean isActive = test(url);
                        //可用+1
                        if(isActive){
                            registry.vote(url);
                        }
                    }
                },VOTE_CHANNEL);
            }
        });
    }

    /**
     * 测试远程服务是否可用
     * @param url 远程服务地址
     * @return true：可用 false：不可用
     */
    private boolean test(String url){
        String[] urlAndPort = url.split(COMMON_SYMBOL_MH);
        if(urlAndPort.length != 2){
            throw new RuntimeException("url格式不正确！");
        }
        Socket socket = new Socket();
        boolean isConnected = false;
        try {
            socket.connect(new InetSocketAddress(urlAndPort[0],Integer.valueOf(urlAndPort[1])),2000); // 建立连接
            isConnected = socket.isConnected();
        }catch (IOException e) {
            return false;
        }finally{
            try {
                socket.close();   // 关闭连接
            } catch (IOException e) {
                return false;
            }
        }
        return isConnected;
    }

}
