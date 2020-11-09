package com.tiger.easyrpc.registry.redis;

import com.tiger.easyrpc.common.RedisMode;
import com.tiger.easyrpc.common.SnowflakeUtils;
import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.config.RegistryConfig;
import com.tiger.easyrpc.registry.IRegistry;
import com.tiger.easyrpc.registry.redis.jedis.SentinelRedisClient;
import com.tiger.easyrpc.registry.redis.jedis.SingleRedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPubSub;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.tiger.easyrpc.common.EasyrpcConstant.*;

/**
 * Reids注册中心，提供发布，删除服务接口
 */
public class RedisRegistry implements IRegistry {
    private Logger logger = LoggerFactory.getLogger(RedisRegistry.class);
    //服务端节点缓存名
    private static final String REGISTRY_CACHE_SERVER = "service";
    //客户端节点缓存名
    private static final String REGISTRY_CACHE_CLIENT = "client";
    private static final String REGISTRY_LOCK = "registry_lock";
    private static final String UNLOCK_SUCCESS = "1";
    private IRedisClient redisClient;
    private static WatchDog watchLock = new WatchDog(1);

    public RedisRegistry(){
        RegistryConfig registryConfig = EasyRpcManager.getInstance().getRegistryConfig();
        String mode = registryConfig.getRedis().getMode();
        if(StringUtils.isEmpty(mode)){
            this.redisClient = new SingleRedisClient();
        }else{
            if(RedisMode.Single.getModeName().equals(mode)){
                this.redisClient = new SingleRedisClient();
            }else if(RedisMode.Sentinel.getModeName().equals(mode)){
                this.redisClient = new SentinelRedisClient();
            }
        }
    }

    @Override
    public Map<String, String> getServiceUrlList() {
        check();
        Map<String, String> serviceUrl = null;
        try{
            serviceUrl = redisClient.hgetAll(REGISTRY_CACHE_SERVER);
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
        String requestId = String.valueOf(SnowflakeUtils.genId());
        check();
        try{
            if(redisClient.lock(requestId)){
                setWatchDog(value);
                if(opr == OPR_REGIST){
                    String arg1 = redisClient.hget(REGISTRY_CACHE_SERVER, key);
                    if(!StringUtils.isEmpty(arg1)){
                        if(arg1.contains(value)) return true;
                        StringBuilder sb = new StringBuilder(arg1);
                        sb.append(COMMON_SYMBOL_DH);
                        sb.append(value);
                        value = sb.toString();
                    }
                    redisClient.hset(REGISTRY_CACHE_SERVER, key, value);
                    logger.info("成功注册服务{}",key);
                }else if(opr == OPR_UNREGIST){
                    String waitRemove = value;
                    Optional.ofNullable(redisClient.hgetAll(REGISTRY_CACHE_SERVER)).ifPresent(services->{
                        services.forEach((service,url)->{
                            if(!StringUtils.isEmpty(url)){
                                if(url.contains(waitRemove)){
                                    String[] redisUrls = url.split(COMMON_SYMBOL_DH);
                                    StringBuilder sb = new StringBuilder();
                                    for (String redisUrl : redisUrls){
                                        if(waitRemove.equals(redisUrl)) continue;
                                        sb.append(redisUrl);
                                        sb.append(COMMON_SYMBOL_DH);
                                    }
                                    if(!StringUtils.isEmpty(sb.toString())){
                                        String putValue = sb.substring(0,sb.length()-1);
                                        redisClient.hset(REGISTRY_CACHE_SERVER, service, putValue);
                                    }else{
                                        redisClient.hdel(REGISTRY_CACHE_SERVER,service);
                                    }
                                    logger.info("成功下线服务{}",service);
                                }
                            }
                        });
                    });
                }else if(opr == OPR_UPDATE){
                    redisClient.hset(REGISTRY_CACHE_SERVER, key, value);
                }
                return true;
         }
        }catch (Exception e){
            throw new RuntimeException("更新服务地址异常！",e);
        }finally {
            redisClient.unlock(requestId);
            watchLock.sleep();
        }
        return false;

    }

    @Override
    public boolean delServiceUrl(String key) {
        check();
        try{
            redisClient.hdel(REGISTRY_CACHE_SERVER,key);
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

    @Override
    public void vote(String key) {
        check();
        String script =
                "if redis.call('exists',KEYS[1]) == 1 then" +
                        "   return redis.call('incr',KEYS[1]) " +
                        "else" +
                        "   return 0 " +
                        "end";
        evalResult(script,Collections.singletonList(key),Collections.singletonList(key));
    }

    @Override
    public boolean startVote(String channel,String url) {
        String script =
                "if redis.call('exists',KEYS[1]) == 1 then" +
                        "   return 0 " +
                        "else" +
                        "   redis.call('set',KEYS[1],0) "+
                        "   return redis.call('publish', ARGV[1] ,KEYS[1]) " +
                        "end";
       return evalResult(script,Collections.singletonList(url),Collections.singletonList(channel));
    }

    private boolean evalResult(String script,List<String> keys,List<String> args){
        Object result = redisClient.eval(script,keys,
                args);
        if(UNLOCK_SUCCESS.equals(result.toString())){
            return true;
        }
        return false;
    }

    @Override
    public String getVoteResult(String key) {
        String s = redisClient.get(key);
        redisClient.del(key);
        return s;
    }

    public void check(){
        if(this.redisClient == null){
            throw new RuntimeException("RedisRegistry缺失IRedisClient对象！");
        }
    }

}
