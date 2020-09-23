package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.serialization.api.ObjectDataInput;
import com.tiger.easyrpc.serialization.protostuff.ProtostuffDataInput;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyProtostuffDec extends LengthFieldBasedFrameDecoder {
    private Class tClass;

    public NettyProtostuffDec(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip,Class tClass) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
        this.tClass = tClass;
    }

    @Override
    public  Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        try {
            byte[] dstBytes = new byte[in.readableBytes()];
            //切记这里一定要用readBytes，不能用getBytes，否则会导致readIndex不能向后移动，从而导致netty did not read anything but decoded a message.错误
            in.readBytes(dstBytes,0,in.readableBytes());
            ObjectDataInput di = new ProtostuffDataInput(dstBytes, tClass);
            return di.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
