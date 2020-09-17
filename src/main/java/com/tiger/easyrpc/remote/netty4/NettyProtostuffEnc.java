package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.serialization.protostuff.ProtostuffDataOutput;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class NettyProtostuffEnc{

    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        int index =  byteBuf.writerIndex();
        ProtostuffDataOutput pdo = new ProtostuffDataOutput();
        byte[] bytes = pdo.writeObject(o);
        byteBuf.writeBytes(bytes);
        int endIndex = byteBuf.writerIndex();
        byteBuf.writeInt(endIndex);
    }
}
