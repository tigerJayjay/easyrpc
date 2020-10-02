package com.tiger.easyrpc.rpc;

import com.tiger.easyrpc.remote.RpcException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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