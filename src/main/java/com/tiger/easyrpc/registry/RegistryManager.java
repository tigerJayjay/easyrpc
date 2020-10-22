package com.tiger.easyrpc.registry;

import com.tiger.easyrpc.common.SysCacheEnum;
import com.tiger.easyrpc.registry.cache.CacheManager;
import com.tiger.easyrpc.registry.cache.CacheTypeEnum;
import com.tiger.easyrpc.registry.cache.ICache;
import com.tiger.easyrpc.registry.redis.RedisRegistry;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tiger.easyrpc.common.EasyrpcConstant.*;

/**
 * 注册中心管理器，用于服务可用性检测，服务发现，服务注册
 */
public class RegistryManager {
    private Logger logger = LoggerFactory.getLogger(RegistryManager.class);
    private static RegistryManager registryManager = new RegistryManager();
    private static ScheduledExecutorService scheculeService = Executors.newScheduledThreadPool(1);
    private static ExecutorService subService = Executors.newFixedThreadPool(2);
    private static final String URL_CHANNEL = "urlChannel";
    private static final String URL_CHANGE = "urlChange";
    private static final String VOTE_CHANNEL = "voteChannel";
    private static final String VOTE_MES = "vote";
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

    public void vote(){
        check();
        this.registry.publish(VOTE_CHANNEL,VOTE_MES);
        logger.info("客户端对连接失败服务发起移除投票！");
    }

    /**
     * 注册中心管理器初始化
     */
    public void init(){
        this.isInit = true;
        this.registry = new RedisRegistry();
        //初始化定时器，移除不可用服务
        scheculeService.scheduleAtFixedRate(new UrlCheckTask(),0,30, TimeUnit.SECONDS);
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
                        boolean isActive = test(message);

                    }
                },VOTE_CHANNEL);
            }
        });
    }

    class UrlCheckTask implements Runnable{
        @Override
        public void run() {
            check();
            Map<String, String> serviceUrlList = registry.getServiceUrlList();
            AtomicBoolean changed = new AtomicBoolean(false);
            for (Map.Entry<String, String> entry : serviceUrlList.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                String[] urls = v.split(COMMON_SYMBOL_DH);
                StringBuilder sb = new StringBuilder();
                for (String url : urls) {
                    if (test(url)) {
                        sb.append(url).append(COMMON_SYMBOL_FH);
                    }else{
                        //发起投票，只要有一个客户端能正常调用服务，说明服务正常，不能从注册中心移除

                    }
                }
                if(sb.length() == 0){
                    registry.delServiceUrl(k);
                    return;
                }
                if (sb.length() > 0) {
                    String redisUrl = sb.substring(0, sb.length() - 1);
                    registry.putServiceUrl(k, redisUrl,OPR_UPDATE);
                    if (!redisUrl.equals(v)) {
                        changed.set(true);
                    }
                }
            }
            //服务url发生变动，发布变动事件
            if(changed.get()){
                registry.publish(URL_CHANNEL,URL_CHANGE);
            }
        }
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
