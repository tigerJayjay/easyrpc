package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.serialization.api.ObjectDataOutput;
import com.tiger.easyrpc.serialization.protostuff.ProtostuffDataOutput;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用protostuff将对象序列化为数组
 */
public class NettyProtostuffEnc extends MessageToByteEncoder {
    private Logger logger = LoggerFactory.getLogger(NettyProtostuffEnc.class);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        ObjectDataOutput pdo = new ProtostuffDataOutput();
        byte[] bytes = pdo.writeObject(o);
        //添加消息头部信息
        byteBuf.writeInt(bytes.length);
        //添加消息数据信息
        byteBuf.writeBytes(bytes);
        logger.trace("发送数据大小{}",byteBuf.readableBytes());
    }
}
