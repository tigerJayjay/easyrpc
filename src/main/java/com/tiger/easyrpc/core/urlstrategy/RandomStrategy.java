package com.tiger.easyrpc.core.urlstrategy;

import java.util.Random;

/**
 * 随机选择策略
 */
public class RandomStrategy implements IStrategy {
    @Override
    public String select(String[] url) {
        //url选取策略,暂时随机
        int length = url.length;
        int select = new Random().nextInt(length);
        return url[select];
    }
}
