package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.core.cache.client.MessageToChannelManager;
import com.tiger.easyrpc.rpc.Result;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tiger.easyrpc.common.EasyrpcConstant.*;

public class NettyClient {
    private Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private EventLoopGroup workerGroup;
    private Bootstrap b;
    private  String host;
    private int port;
    private ChannelFuture channelFuture;
    public NettyClient(String url){
        resolverUrl(url);
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
                //解码
                socketChannel.pipeline().addLast(new NettyProtostuffDec(1024 * 1024, 0, 4,0,4,Result.class));
                //编码
                socketChannel.pipeline().addLast(new NettyProtostuffEnc());
                socketChannel.pipeline().addLast(new TcpHalfPackageTestHandler());
            }
        });

        channelFuture = b.connect(host,port).sync();
    }

    public void close(){
        this.channelFuture.channel().close();
    }

    public void sendMessage(Object object){
        this.channelFuture.channel().writeAndFlush(object);
    }

    public class TcpHalfPackageTestHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
          MessageToChannelManager.messageToChannel.get(((Result)msg).getMesId()).receiveMessage(((Result)msg).getResult());
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }
    }





}
