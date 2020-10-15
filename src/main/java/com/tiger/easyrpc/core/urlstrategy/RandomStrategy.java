package com.tiger.easyrpc.core.urlstrategy;

/**
 * 随机选择策略
 */
public class RandomStrategy implements IStrategy {
    @Override
    public String select(String[] url) {
        //url选取策略,暂时随机
        int length = url.length-1;
        int select = (int)(Math.random()*length);
        return url[select];
    }
}
