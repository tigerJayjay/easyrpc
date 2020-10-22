package com.tiger.easyrpc.registry.redis;

import java.util.Collections;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 监听分布式锁，如果业务执行时间较长，定时为分布式锁续期，如果业务逻辑正常执行完毕或异常，该监视器会进入睡眠状态
 * 不再为分布式锁续期，直到下个线程正常执行为止
 */
public class WatchDog extends ScheduledThreadPoolExecutor {
    private volatile boolean isWork;
    private volatile boolean init;
    private volatile String watchKey;
    private static final String UNLOCK_SUCCESS = "1";
    public WatchDog(int corePoolSize) {
        super(corePoolSize);
    }

    /**
     * 监视器进入睡眠状态
     */
    public void sleep(){
        this.isWork = false;
    }

    /**
     * 监视器进入工作状态
     */
    public void work(){
        this.isWork = true;
    }

    public boolean isInit(){
        return init;
    }


    public void init(IRedisClient redisClient,String registryLock){
        Runnable task = ()-> {
            try {
                if(!this.isWork){
                    return;
                }
                String script =
                        "if redis.call('get',KEYS[1]) == ARGV[1] then" +
                                "   return redis.call('expire',KEYS[1],30000) " +
                                "else" +
                                "   return 0 " +
                                "end";
                Object result = redisClient.eval(script, Collections.singletonList(registryLock),
                        Collections.singletonList(watchKey));
                if (UNLOCK_SUCCESS.equals(result.toString())) {
                    System.out.println("增加存活时间");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        };
        this.scheduleAtFixedRate(task,0,10, TimeUnit.SECONDS);
        this.init = true;
    }

    public void setWatchKey(String watchKey){
        this.watchKey = watchKey;
    }
}
