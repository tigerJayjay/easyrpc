package com.tiger.easyrpc.rpc;

import com.tiger.easyrpc.remote.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 保存异步调用信息，客户端发送消息可获得此对象，并通过此对象获取异步调用结果是否已经返回
 */
public class ResultFuture {
    private ReentrantLock lock = new ReentrantLock();
    private Condition getResult = lock.newCondition();
    private volatile Object result;
    private volatile boolean hasResult = false;
    public Object getResult(long timeout) throws RpcException{
        try{
            lock.lock();
            if(hasResult){
                return result;
            }
            boolean await = getResult.await(timeout, TimeUnit.MILLISECONDS);
            if(!await){
                throw new RpcException("远程调用超时！",null);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }finally {
            lock.unlock();
        }
        return result;
    }

    public void setResult(Object result) {
        try{
            lock.lock();
            this.result = result;
            this.hasResult = true;
            getResult.signalAll();
        }finally {
            lock.unlock();
        }
    }
}