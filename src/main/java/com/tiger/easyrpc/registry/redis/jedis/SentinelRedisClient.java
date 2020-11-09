package com.tiger.easyrpc.registry.redis.jedis;

import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.config.RedisConfig;
import com.tiger.easyrpc.core.config.RegistryConfig;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tiger.easyrpc.common.EasyrpcConstant.COMMON_SYMBOL_DH;

public class SentinelRedisClient extends AbstractRedisClient {
    public SentinelRedisClient(){
        RegistryConfig registryConfig = EasyRpcManager.getInstance().getRegistryConfig();
        RedisConfig redis = registryConfig.getRedis();
        checkParam(redis);
        pool = new JedisSentinelPool(redis.getMasterName(),
                getSentinelUrlSet(redis.getSentinelUrl()),getPoolConfig(),
                registryConfig.getRedis().getTimeout(),
                registryConfig.getRedis().getPassword());
    }

    private Set<String> getSentinelUrlSet(String urlStr){
        String[] split = urlStr.split(COMMON_SYMBOL_DH);
        List<String> urls = Arrays.asList(split);
        return new HashSet<>(urls);
    }

    private void checkParam(RedisConfig redis){
        if(StringUtils.isEmpty(redis.getMasterName())){
            throw new RuntimeException("未配置easyrpc.registry.redis.masterName参数！");
        }
        if(StringUtils.isEmpty(redis.getSentinelUrl())){
            throw new RuntimeException("未配置easyrpc.registry.redis.sentinelUrl参数！");
        }
    }

}
