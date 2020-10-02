package com.tiger.easyrpc.rpc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ResultFuture {
    private ReentrantLock lock = new ReentrantLock();
    private Condition getResult = lock.newCondition();
    private volatile Object result;
    private volatile boolean hasResult = false;
    public Object getResult(long timeout) {
        try{
            lock.lock();
            if(hasResult){
                return result;
            }
            getResult.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
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