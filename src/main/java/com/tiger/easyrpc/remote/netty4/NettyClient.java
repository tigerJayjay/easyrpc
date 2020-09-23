package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.rpc.api.Parameter;
import com.tiger.easyrpc.rpc.api.Result;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

import static com.tiger.easyrpc.common.EasyrpcConstant.*;

public class NettyClient {
    private EventLoopGroup workerGroup;
    private Bootstrap b;
    private  String host;
    private int port;
    private Parameter parameter;
    private ResultFuture resultFuture;
    public NettyClient(String url,Parameter parameter){
        resolverUrl(url);
        this.parameter = parameter;
    }

    private void resolverUrl(String url){
        try{
            String[] split = url.split(COMMON_SYMBOL_MH);
            if(split.length != 2){
                throw new RuntimeException("url格式错误！");
            }
            this.host = split[0];
            this.port = Integer.valueOf(split[1]);
        }catch (Exception e){
            throw new RuntimeException("url格式错误！",e);
        }
    }
    public  ResultFuture connect() throws Exception{
        resultFuture = new ResultFuture();
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
                    socketChannel.pipeline().addLast(new NettyProtostuffDec(1024 * 1024, 0, 4,0,4,Result.class));
                    //编码
                    socketChannel.pipeline().addLast(new NettyProtostuffEnc());
                    socketChannel.pipeline().addLast(new TcpHalfPackageTestHandler());
                }
            });
            ChannelFuture future=b.connect(host,port).sync();
            future.channel().closeFuture();
        }finally {
            workerGroup.shutdownGracefully().sync();
        }
        return resultFuture;
    }

    public class TcpHalfPackageTestHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            Parameter p = NettyClient.this.parameter;
            ctx.writeAndFlush(p);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            try {
                resultFuture.setStatus(RESULT_BACK_SUC);
                resultFuture.setResult(((Result)msg).getResult());
            } finally {
                ReferenceCountUtil.release(msg);
            }
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }
    }

    public class ResultFuture{
        private int status;
        private Object result;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }
    }





}
