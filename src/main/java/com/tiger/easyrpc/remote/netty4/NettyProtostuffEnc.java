package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.serialization.api.ObjectDataOutput;
import com.tiger.easyrpc.serialization.protostuff.ProtostuffDataOutput;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
/**
 * 使用protostuff将对象序列化为数组
 */
public class NettyProtostuffEnc extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        ObjectDataOutput pdo = new ProtostuffDataOutput();
        byte[] bytes = pdo.writeObject(o);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
