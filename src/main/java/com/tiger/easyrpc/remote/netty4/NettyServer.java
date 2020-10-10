package com.tiger.easyrpc.remote.netty4;

import com.tiger.easyrpc.core.cache.server.ExportServiceManager;
import com.tiger.easyrpc.core.util.SpringBeanUtils;
import com.tiger.easyrpc.remote.RpcException;
import com.tiger.easyrpc.rpc.Parameter;
import com.tiger.easyrpc.rpc.Result;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static com.tiger.easyrpc.common.EasyrpcConstant.COMMON_SYMBOL_MH;
import static com.tiger.easyrpc.common.EasyrpcConstant.DATA_TYPE_IDLE;

/**
 * netty服务端类，通过此类相应客户端调用，并通过客户端调用信息，调用服务对象并返回调用结果
 */
public class NettyServer {
    private Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    /**
     * 设置服务监听
     * @throws Exception
     */
    public void run() throws  Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)//调用channel()方法通过new ReflectiveChannelFactory(channelClass)实例化channel工厂
                .childOption(NioChannelOption.TCP_NODELAY,true)
                .option(NioChannelOption.SO_BACKLOG,1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new IdleStateHandler(40, 0, 60 * 10, TimeUnit.SECONDS));
                        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024*1024,0,4,0,4));
                        //解码
                        socketChannel.pipeline().addLast(new NettyProtostuffDec(Parameter.class));
                        //编码
                        socketChannel.pipeline().addLast(new NettyProtostuffEnc());
                        socketChannel.pipeline().addLast(new NettyServerHandler());
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE,true);
        //绑定端口，开始接收即将到来的连接
        ChannelFuture f = b.bind(port).sync();
    }

    /**
     * 调用信息处理类
     */
    class NettyServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if(evt instanceof IdleStateEvent){
                IdleStateEvent event = (IdleStateEvent)evt;
                if(event.state().equals(IdleState.READER_IDLE)){
                    ctx.channel().close();
                }
            }else {
                super.userEventTriggered(ctx, evt);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg){
            Result result = new Result();
            try{
                Parameter p = (Parameter)msg;
                //心跳信息不处理
                if(p.getType() == DATA_TYPE_IDLE){
                    return;
                }else {
                    setInvokeData(p, result);
                }
                ctx.writeAndFlush(result);
            }catch (Exception e){
                result.setException(new RpcException("远程调用异常！",e));
                ctx.writeAndFlush(result);
                logger.error("服务处理异常！",e);
            }finally{
                ReferenceCountUtil.release(msg);
            }
        }

        /**
         * 调用服务，并组装返回信息
         * @param p 调用参数
         * @param result 调用结果
         * @throws NoSuchMethodException
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         */
        private void setInvokeData(Parameter p,Result result) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            Class aClass = ExportServiceManager.services.get(p.getClazz().getName() +
                    COMMON_SYMBOL_MH + p.getVersion() + COMMON_SYMBOL_MH + p.getGroup());
            Object bean = SpringBeanUtils.getBean(aClass);
            Method method = bean.getClass().getMethod(p.getMethod().getName(),p.getMethod().getParameterTypes());
            Object invoke = method.invoke(bean,p.getObjs());
            result.setResult(invoke);
            result.setMesId(p.getMesId());
        }
    }
}

