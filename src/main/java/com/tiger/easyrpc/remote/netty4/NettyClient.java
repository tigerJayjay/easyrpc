package com.tiger.easyrpc.remote.netty4;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class NettyClient {
    private EventLoopGroup workerGroup;
    private Bootstrap b;
    private Channel channel;
    private  String host = "132.232.103.107";
    private int port = 8082;
    public  void connect() throws Exception{
        workerGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        //bossGroup不用于客户端
        b.group(workerGroup);
        //服务端用的是NioServerSocketChannel
        b.channel(NioSocketChannel.class);
        //没有使用childOption()，因为客户端SocketChannel没有parent
        b.option(ChannelOption.SO_KEEPALIVE,true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ByteBuf delimiter = Unpooled.copiedBuffer("$".getBytes());
                socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
                socketChannel.pipeline().addLast(new IdleStateHandler(0,0,30));
            }
        });
        doConnect();
    }
    public void doConnect(){
        if(channel!=null&&channel.isActive()){
            return;
        }
        ChannelFuture f= b.connect(host,port);
        f.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){
                    channel = channelFuture.channel();
                    System.out.println("连接服务器成功");
                }else{
                    System.out.println("10秒后重连");
                    channelFuture.channel().eventLoop().schedule(new Runnable() {
                        public void run() {
                            doConnect();
                        }
                    },10, TimeUnit.SECONDS);
                }
            }
        });
    }
}
