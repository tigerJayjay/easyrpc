package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.config.util.SpringBeanUtils;
import com.tiger.easyrpc.rpc.api.Parameter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class NettyServer {
    private Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void run() throws  Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)//调用channel()方法通过new ReflectiveChannelFactory(channelClass)实例化channel工厂
                    .childOption(NioChannelOption.TCP_NODELAY,true)
                    .option(NioChannelOption.SO_BACKLOG,1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //解码
                            socketChannel.pipeline().addLast(new NettyProtostuffDec(1024 * 1024, 0, 4,0,4));
                            //编码
                            socketChannel.pipeline().addLast(new NettyProtostuffEnc());
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            //绑定端口，开始接收即将到来的连接
            ChannelFuture f = b.bind(port).sync();//->doBind()->initAndRegister()->
            //等待服务端socket被关闭
            //在这个例子中，不会发生，但是你可以通过这样做去优雅的关闭你的服务端
            f.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    class NettyServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg){
            try{
                Parameter p = (Parameter)msg;
                Object bean = SpringBeanUtils.getBean(p.getClazz());
                Method method = bean.getClass().getMethod(p.getMethod().getName(),p.getMethod().getParameterTypes());
                Object invoke = method.invoke(bean,p.getObjs());
                ctx.writeAndFlush(invoke);
            }catch (Exception e){
                logger.error("服务处理异常！",e);
            }finally{
                ReferenceCountUtil.release(msg);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            // TODO Auto-generated method stub
            super.exceptionCaught(ctx, cause);

        }
        /**
         * 心跳检测触发的事件通过本方法捕获
         */
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleState state = ((IdleStateEvent) evt).state();
                if(state == state.READER_IDLE ){
                    ChannelFuture fcutrue =  ctx.close();
                    fcutrue.addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            Channel channel = future.channel();

                        }
                    });
                }
            }else{
                super.userEventTriggered(ctx, evt);
            }
        }
    }
}

