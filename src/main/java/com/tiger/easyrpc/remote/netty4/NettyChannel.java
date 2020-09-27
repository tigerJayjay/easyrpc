package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.remote.api.Channel;

import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class NettyChannel implements Channel {
    private String url;
    private static HashMap<String,NettyClient> clientMap = new HashMap<>();
    private ResultFuture resultFuture;
    public NettyChannel(String url){
        this.url = url;
        this.resultFuture = new ResultFuture();
    }
    public String getUrl(){
        return this.url;
    }

    public ResultFuture getResultFuture(){
        return this.resultFuture;
    }

    @Override
    public void sendMessage(Object o) throws Exception {
        synchronized (clientMap){
            NettyClient client = clientMap.get(url);
            if(client == null){
                client = new NettyClient(url);
                client.connect();
                clientMap.put(url,client);
            }
            client.sendMessage(o);
        }
    }

    @Override
    public void receiveMessage(Object o) {
        this.resultFuture.setResult(o);
    }


    public class ResultFuture {
        private ReentrantLock lock = new ReentrantLock();
        private Condition getResult = lock.newCondition();
        private volatile Object result;
        private volatile boolean hasResult = false;
        public Object getResult() {
            try{
                lock.lock();
                if(hasResult){
                    return result;
                }
                try {
                    getResult.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

}
