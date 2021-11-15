package com.sumscope.netty.connector.client.infrastructure;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

/**
 * @author pjmike
 * @create 2018-10-24 16:31
 */
@Component
public class NettyClient {
    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Value("${netty.port}")
    private int port;

    @Value("${netty.host}")
    private String host;

    private final EventLoopGroup group = new NioEventLoopGroup();

    private boolean isConnect;

    private SocketChannel socketChannel;


    /**
     * 发送数据
     *
     * @param byteBuf 消息字节数组
     */
    public void sendMsg(ByteBuf byteBuf) {
        logger.info("send messag : {}", byteBuf);
        socketChannel.writeAndFlush(byteBuf);
    }


    /**
     * 启动 netty 客户端
     */
    @PostConstruct
    public void start() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new ProtobufVarint32FrameDecoder())
                                //.addLast(new ProtobufDecoder(UserInfo.UserMsg.getDefaultInstance()))
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufEncoder())
                                .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                                .addLast(new NettyClientHeartbeatHandler())
                                .addLast(new NettyClientHandler());
                    }
                });
        ChannelFuture future = bootstrap.connect();
        //客户端断线重连逻辑
        future.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                isConnect = true;
                logger.info("连接服务器：{}:{} 成功...", host, port);
            } else {
                isConnect = false;
                logger.error("连接服务器：{}:{} 失败...", host, port);
                future1.channel().eventLoop().schedule(this::start, 20, TimeUnit.SECONDS);
            }
        });
        socketChannel = (SocketChannel) future.channel();
    }


    public boolean isConnect() {
        return this.isConnect;
    }
}
