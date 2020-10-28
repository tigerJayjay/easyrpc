package com.tiger.easyrpc.common;

import java.io.File;
import java.util.Random;

/**
 * Easyrpc常量类
 */
public class EasyrpcConstant {
    public static final String COMMON_SYMBOL_DH = ",";
    public static final String COMMON_SYMBOL_FH = ";";
    public static final String COMMON_SYMBOL_MH = ":";
    public static final String COMMON_SYMBOL_XG = File.separator;
    public static final String COMMON_SYMBOL_YJH = ".";
    public static final String EMPTY_STR = "";

    //RPC参数对象数据类型，0：心跳数据 1：远程调用数据
    public static final int DATA_TYPE_IDLE = 0;
    public static final int DATA_TYPE_INVOKE = 1;

    //服务端重连次数
    public static final int CONNECT_RETRY_COUNT = 3;
    //重连间隔
    public static final int CONNECT_RETRY_INTERVAL = new Random().nextInt(5);
    //扫描无效客户端间隔时间
    public static final long NO_CONNECT_CLIENT_SCAN_INTERVAL = 30000;

    //客户端对象状态
    public static final int CLIENT_STATUS_NEW = 0;
    public static final int CLIENT_STATUS_INIT = 1;
    public static final int CLIENT_STATUS_CONNECT = 2;
    public static final int CLIENT_STATUS_DISCONNECT = 3;
    public static final int CLIENT_STATUS_DIE = 4;

    //注册中心 服务操作类型
    public static final int OPR_REGIST = 0; //新增
    public static final int OPR_UNREGIST = 1;//注册
    public static final int OPR_UPDATE = 2; //更新

}
