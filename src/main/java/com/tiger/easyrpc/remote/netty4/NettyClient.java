package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.core.cache.client.MessageToChannelManager;
import com.tiger.easyrpc.remote.api.Client;
import com.tiger.easyrpc.rpc.Parameter;
import com.tiger.easyrpc.rpc.Result;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tiger.easyrpc.common.EasyrpcConstant.*;

/**
 * Netty远程连接客户端类，直接与服务端进行数据传输
 */
public class NettyClient implements Client {
    private AtomicInteger retryCount = new AtomicInteger(0);
    private volatile int status;
    private Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private EventLoopGroup workerGroup;
    private Bootstrap b;
    private  String host;
    private int port;
    private volatile ChannelFuture channelFuture;
    public NettyClient(String url){
        resolverUrl(url);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isConnected() {
        return this.status == CLIENT_STATUS_CONNECT;
    }

    /**
     * 检测url地址是否合法
     * @param url
     */
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

    /**
     * 客户端连接初始化
     */
    public void init(){
        status = CLIENT_STATUS_INIT;
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
                socketChannel.pipeline().addLast(new IdleStateHandler(20, 10, 60 * 10, TimeUnit.SECONDS));
                socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024*1024,0,4,0,4));
                //解码
                socketChannel.pipeline().addLast(new NettyProtostuffDec(Result.class));
                //编码
                socketChannel.pipeline().addLast(new NettyProtostuffEnc());
                socketChannel.pipeline().addLast(new NettyClientHandler());
            }
        });
    }

    /**
     * 连接远程服务
     * @throws Exception
     */
    public synchronized void connect(){
        if(this.status < CLIENT_STATUS_INIT){
            init();
        }
        if(this.isConnected()){
            return;
        }
        try {
            channelFuture = b.connect(host,port).sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 连接重试
     */
    public void retryConnect(){
//        while(!isConnected() && status != CLIENT_STATUS_DIE){
//            retryCount.getAndIncrement();
//            try {
//                Thread.sleep(new Random().nextInt(CONNECT_RETRY_INTERVAL)+1);
//                this.connect();
//                return;
//            }catch (Exception e){
//                logger.info("重连服务第{}次！",retryCount.get());
//            }
//        }
        channelFuture = b.connect(host,port);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    final EventLoop loop = channelFuture.channel().eventLoop();
                    loop.schedule(new Runnable() {
                        @Override
                        public void run() {
                            logger.info("重连服务第{}次！",retryCount.addAndGet(1));
                            try {
                                retryConnect();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 1L, TimeUnit.SECONDS);
                }
            }
        });
    }

    /**
     * 关闭连接
     * @throws InterruptedException
     */
    public void close(){
        Channel channel = this.channelFuture.channel();
        if(channel.isOpen()){
            channel.close();
        }
        this.status = CLIENT_STATUS_DIE;
    }

    /**
     * 发送信息
     * @param object
     */
    public  void  sendMessage(Object object){
        System.out.println("writeflushbefore:"+System.currentTimeMillis());
        this.channelFuture.channel().writeAndFlush(object);
        System.out.println("writeflushend:"+System.currentTimeMillis());
    }

    /**
     * 消息处理器
     */
    public class NettyClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if(evt instanceof IdleStateEvent){
                IdleStateEvent event = (IdleStateEvent)evt;
               if(event.state().equals(IdleState.WRITER_IDLE)){
                    Parameter idle = new Parameter();
                    idle.setType(DATA_TYPE_IDLE);
                    ctx.writeAndFlush(idle);
                }
            }else {
                super.userEventTriggered(ctx, evt);
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            NettyClient.this.status = CLIENT_STATUS_CONNECT;
            //重置重试次数
            retryCount.set(0);
            logger.info("连接成功！");
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            NettyClient.this.status = CLIENT_STATUS_DISCONNECT;
            retryConnect();
            logger.info("连接断开！");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg){
            System.out.println("writereadbefore:"+System.currentTimeMillis());
            Result res = (Result)msg;
            //返回心跳信息，不处理
            if(res.getType() == DATA_TYPE_IDLE){
                return;
            }
            MessageToChannelManager.messageToChannel.get(((Result)msg).getMesId()).receiveMessage(res.getResult());
            if(res.getException() != null){
                throw new RuntimeException(res.getException());
            }
            System.out.println("writereadend:"+System.currentTimeMillis());

        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx,cause);
        }

    }
}
