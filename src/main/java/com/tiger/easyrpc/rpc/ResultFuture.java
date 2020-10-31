package com.tiger.easyrpc.rpc;

import com.tiger.easyrpc.remote.RpcException;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 保存异步调用信息，客户端发送消息可获得此对象，并通过此对象获取异步调用结果是否已经返回
 */
public class ResultFuture {
    private Semaphore semaphore = new Semaphore(0);
    private volatile Object result;
    public Object getResult(long timeout) throws RpcException{
        System.out.println("getresbefore:"+System.currentTimeMillis());
        try{
            boolean await =  semaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS);
            if(!await){
                throw new RpcException("远程调用超时！",null);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("getresend:"+System.currentTimeMillis());
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
        semaphore.release();
    }
}