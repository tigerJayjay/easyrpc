package com.tiger.easyrpc.core.config;

import com.tiger.easyrpc.core.EasyRpcManager;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 注册中心配置类
 */
@ConfigurationProperties("easyrpc.registry")
public class RegistryConfig {
    public RegistryConfig(){
        EasyRpcManager.getInstance().setRegistryConfig(this);
    }
    private RedisConfig redis;
    private Long voteWait;
    private Long voteInterval;


    public Long getVoteWait() {
        return voteWait;
    }

    public void setVoteWait(Long voteWait) {
        this.voteWait = voteWait;
    }

    public Long getVoteInterval() {
        return voteInterval;
    }

    public void setVoteInterval(Long voteInterval) {
        this.voteInterval = voteInterval;
    }

    public RedisConfig getRedis() {
        return redis;
    }

    public void setRedis(RedisConfig redis) {
        this.redis = redis;
    }
}
