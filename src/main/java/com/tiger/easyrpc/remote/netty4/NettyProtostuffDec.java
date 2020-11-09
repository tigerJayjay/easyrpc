package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.serialization.api.ObjectDataInput;
import com.tiger.easyrpc.serialization.protostuff.ProtostuffDataInput;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 使用protostuff将byte数组反序列化为对象
 */
public class NettyProtostuffDec extends ByteToMessageDecoder {
    private Logger logger = LoggerFactory.getLogger(NettyProtostuffDec.class);
    private Class tClass;

    public NettyProtostuffDec(Class tClass) {
        this.tClass = tClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        try {
            byte[] dstBytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(dstBytes,0,byteBuf.readableBytes());
            logger.trace("收到数据大小{}",dstBytes.length);
            ObjectDataInput di = new ProtostuffDataInput(dstBytes, tClass);
            list.add(di.readObject()) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
