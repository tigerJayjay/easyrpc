package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.demo.interfaces.ITest;
import com.tiger.easyrpc.rpc.api.Parameter;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private EventLoopGroup workerGroup;
    private Bootstrap b;
    private  String host = "127.0.0.1";
    private int port = 16388;
    private Parameter parameter;
    public NettyClient(String url,Parameter parameter){

    }
    public  void connect() throws Exception{
        try{
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
                    //解码
                    socketChannel.pipeline().addLast(new NettyProtostuffDec(1024 * 1024, 0, 4,0,4));
                    //编码
                    socketChannel.pipeline().addLast(new NettyProtostuffEnc());
                    socketChannel.pipeline().addLast(new TcpHalfPackageTestHandler());
                }
            });
            ChannelFuture future=b.connect().sync();
            future.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully().sync();
        }
    }

    class TcpHalfPackageTestHandler extends ChannelInboundHandlerAdapter {

        public TcpHalfPackageTestHandler() {

        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            Parameter p = new Parameter();
            p.setClazz(ITest.class);
            try {
                p.setMethod(ITest.class.getMethod("test",new Class[]{String.class}));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            p.setObjs(new Object[]{"test"});
            ctx.writeAndFlush(p);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            try {

            } finally {
                ReferenceCountUtil.release(msg);
            }
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }
    }

    }
