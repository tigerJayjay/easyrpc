package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.core.EasyRpcManager;
import com.tiger.easyrpc.core.cache.client.MessageToChannelManager;
import com.tiger.easyrpc.core.config.ConsumerConfig;
import com.tiger.easyrpc.remote.RpcException;
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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.tiger.easyrpc.common.EasyrpcConstant.*;

/**
 * Netty远程连接客户端类，直接与服务端进行数据传输
 */
public class NettyClient implements Client {
    private  ReentrantLock lock = new ReentrantLock();
    private  Condition connected = lock.newCondition();
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
                socketChannel.pipeline().addLast(new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS));
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
        logger.trace("{}开始连接client{}！",Thread.currentThread().getName(),this);
        if(this.status < CLIENT_STATUS_INIT){
            init();
        }
        //多个线程可能同时对同一个client对象进行连接操作，如果其中一个线程已经连接成功
        //其他线程直接退出
        if(this.isConnected()){
            return;
        }
        channelFuture = b.connect(host,port);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                //该client对象生命周期状态已经死亡或者超过配置重试次数，不再重试
                ConsumerConfig consumerConfig = EasyRpcManager.getInstance().getConsumerConfig();
                if((consumerConfig.getRetryCount()!=null &&
                        retryCount.get()>consumerConfig.getRetryCount())||
                        NettyClient.this.status == CLIENT_STATUS_DIE){
                    return;
                }
                if (!channelFuture.isSuccess()) {
                    NettyClient.this.status = CLIENT_STATUS_DISCONNECT;
                    final EventLoop loop = channelFuture.channel().eventLoop();
                    Long retryInterval = DEFAULT_RETRY_INTERVAL;
                    Long configRetryInterval = consumerConfig.getRetryInterval();
                    if(configRetryInterval != null){
                        retryInterval = configRetryInterval;
                    }
                    loop.schedule(new Runnable() {
                        @Override
                        public void run() {
                            logger.info("重连服务第{}次！",retryCount.addAndGet(1));
                            try {
                                connect();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, retryInterval, TimeUnit.MILLISECONDS);
                }

            }
        });
    }

    /**
     * 连接重试
     */
    public void retryConnect(){
        channelFuture = b.connect(host,port);

    }

    /**
     * 关闭连接
     * @throws InterruptedException
     */
    public void close(){
        this.status = CLIENT_STATUS_DIE;
        Channel channel = this.channelFuture.channel();
        if(channel.isOpen()){
            channel.close();
        }
    }

    /**
     * 发送信息
     * @param object
     */
    public  void  sendMessage(Object object) throws RpcException {
        //并发情况下，可能会出现一个线程对client对象发起连接的过程中，另一个线程获取到该client发送信息，此时由于client还未
        //成功连接到客户端，发送消息会失败，所以在这里如果client状态为还未成功连接，需要等待该客户端连接成功之后才能发送
        long start = System.currentTimeMillis();
        while(!(System.currentTimeMillis() - start > 3000)){
            if(isConnected()){
                logger.trace("{}发送！",Thread.currentThread().getName());
                this.channelFuture.channel().writeAndFlush(object);
                return;
            }else if(this.status == CLIENT_STATUS_DIE) {
                throw new RpcException("客户端对象已失效！");
            }else{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


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
            logger.info("连接断开！");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg){
            Result res = (Result)msg;
            //返回心跳信息，不处理
            if(res.getType() == DATA_TYPE_IDLE){
                return;
            }
            MessageToChannelManager.messageToChannel.get(((Result)msg).getMesId()).receiveMessage(res.getResult());
            if(res.getException() != null){
                throw new RuntimeException(res.getException());
            }

        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx,cause);
        }

    }
}
