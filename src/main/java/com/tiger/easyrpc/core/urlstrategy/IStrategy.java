package com.tiger.easyrpc.core.urlstrategy;

/**
 * 远程服务选择策略接口
 */
public interface IStrategy {
    String select(String[] url);
}
