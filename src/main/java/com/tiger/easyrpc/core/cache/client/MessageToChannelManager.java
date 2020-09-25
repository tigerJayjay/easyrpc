package com.tiger.easyrpc.core.cache.client;

import com.tiger.easyrpc.remote.api.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存消息和通道对应信息
 */
public class MessageToChannelManager {
    public static Map<String, Channel> messageToChannel = new ConcurrentHashMap<String,Channel>();

}
