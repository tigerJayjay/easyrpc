package com.tiger.easyrpc.core.urlstrategy;
public class RandomStrategy implements IStrategy {
    @Override
    public String select(String[] url) {
        //url选取策略,暂时随机
        int length = url.length-1;
        int select = (int)(Math.random()*length+1);
        return url[select];
    }
}
