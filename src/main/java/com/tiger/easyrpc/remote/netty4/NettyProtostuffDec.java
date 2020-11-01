package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.serialization.api.ObjectDataInput;
import com.tiger.easyrpc.serialization.protostuff.ProtostuffDataInput;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 使用protostuff将byte数组反序列化为对象
 */
public class NettyProtostuffDec extends ByteToMessageDecoder {
    private Class tClass;

    public NettyProtostuffDec(Class tClass) {
        this.tClass = tClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        try {
            byte[] dstBytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(dstBytes,0,byteBuf.readableBytes());
            ObjectDataInput di = new ProtostuffDataInput(dstBytes, tClass);
            list.add(di.readObject()) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
