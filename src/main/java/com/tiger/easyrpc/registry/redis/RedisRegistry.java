package com.tiger.easyrpc.registry.redis;

import com.tiger.easyrpc.registry.IRegistry;
import com.tiger.easyrpc.registry.redis.jedis.SingleRedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.Map;

import static com.tiger.easyrpc.common.EasyrpcConstant.*;

/**
 * Reids注册中心，提供发布，删除服务接口
 */
public class RedisRegistry implements IRegistry {
    private Logger logger = LoggerFactory.getLogger(RedisRegistry.class);
    private static final String REGISTRY_CACHE_NAME = "service";
    private static final String REGISTRY_LOCK = "registry_lock";
    private static final String LOCK_SUCCESS = "OK";
    private static final String UNLOCK_SUCCESS = "1";
    private long expireTime = 30000;//锁过期时间
    private IRedisClient redisClient;
    private static WatchDog watchLock = new WatchDog(1);

    public RedisRegistry(){
        this.redisClient = new SingleRedisClient();
    }

    @Override
    public Map<String, String> getServiceUrlList() {
        check();
        Map<String, String> serviceUrl = null;
        try{
            serviceUrl = redisClient.hgetAll(REGISTRY_CACHE_NAME);
        }catch(Exception e){
            throw new RuntimeException("获取注册服务地址异常！",e);
        }
        return serviceUrl;
    }

    private void setWatchDog(String value){
        watchLock.setWatchKey(value);
        watchLock.work();
       if(!watchLock.isInit()){
           watchLock.init(redisClient,REGISTRY_LOCK);
       }
    }

    @Override
    public boolean putServiceUrl(String key, String value,int opr) {
        check();
        try{
            if(lock(value)){
                System.out.println(value);
                setWatchDog(value);
                if(opr == OPR_REGIST){
                    String arg1 = redisClient.hget(REGISTRY_CACHE_NAME, key);
                    if(!StringUtils.isEmpty(arg1)){
                        if(arg1.contains(value)) return true;
                        StringBuilder sb = new StringBuilder(arg1);
                        sb.append(COMMON_SYMBOL_DH);
                        sb.append(value);
                        value = sb.toString();
                    }
                    redisClient.hset(REGISTRY_CACHE_NAME, key, value);
                    logger.info("成功注册服务{}",key);
                }else if(opr == OPR_UNREGIST){
                    Map<String, String> services = redisClient.hgetAll(REGISTRY_CACHE_NAME);
                    final String waitRemove = value;
                    services.forEach((service,url)->{
                        if(!StringUtils.isEmpty(url)){
                            if(url.contains(waitRemove)){
                                String[] redisUrls = url.split(COMMON_SYMBOL_DH);
                                StringBuilder sb = new StringBuilder();
                                for (String redisUrl : redisUrls){
                                    if(waitRemove.equals(redisUrl)) continue;
                                    sb.append(waitRemove);
                                    sb.append(COMMON_SYMBOL_DH);
                                }
                                if(!StringUtils.isEmpty(sb.toString())){
                                    String putValue = sb.substring(0,sb.length()-1);
                                    redisClient.hset(REGISTRY_CACHE_NAME, service, putValue);
                                }else{
                                    redisClient.hdel(REGISTRY_CACHE_NAME,service);
                                }
                                logger.info("成功下线服务{}",service);
                            }
                        }
                    });

                }else if(opr == OPR_UPDATE){
                    redisClient.hset(REGISTRY_CACHE_NAME, key, value);
                }
                return true;
         }
        }catch (Exception e){
            throw new RuntimeException("更新服务地址异常！",e);
        }finally {
            unlock(value);
            watchLock.sleep();
        }
        return false;

    }

    @Override
    public boolean delServiceUrl(String key) {
        check();
        try{
            redisClient.hdel(REGISTRY_CACHE_NAME,key);
            logger.info("移除服务{}",key);
        }catch (Exception e){
            throw new RuntimeException("移除注册中心服务失败！",e);
        }
        return true;
    }

    @Override
    public void subscribe(JedisPubSub jedisPubSub,String channel) {
        check();
        redisClient.subscribe(jedisPubSub,channel);
    }

    @Override
    public void publish(String channel,String mes) {
        check();
        redisClient.publish(channel,mes);
    }

    /**
     * redis分布式加锁
     * @param requestId  解锁时用于区分该锁是否为该线程加的锁
     * @return
     */
    private  boolean lock(String requestId) {
        check();
        //SET命令的参数
        SetParams params = SetParams.setParams().nx().px(expireTime);
        String result = redisClient.set(REGISTRY_LOCK, requestId,params);
        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    /**
     * redis分布式解锁
     * @param requestId
     * @return
     */
    public boolean unlock(String requestId){
        check();
        String script =
                "if redis.call('get',KEYS[1]) == ARGV[1] then" +
                        "   return redis.call('del',KEYS[1]) " +
                        "else" +
                        "   return 0 " +
                        "end";
        Object result = redisClient.eval(script, Collections.singletonList(REGISTRY_LOCK),
                Collections.singletonList(requestId));
        if(UNLOCK_SUCCESS.equals(result.toString())){
            return true;
        }
        return false;
    }

    public void check(){
        if(this.redisClient == null){
            throw new RuntimeException("RedisRegistry缺失IRedisClient对象！");
        }
    }

}
