package com.tiger.easyrpc.remote;

/**
 * 远程调用异常
 */
public class RpcException extends Throwable {
    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
