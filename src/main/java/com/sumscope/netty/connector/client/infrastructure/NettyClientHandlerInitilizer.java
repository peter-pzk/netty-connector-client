package com.sumscope.netty.connector.client.infrastructure;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author pjmike
 * @create 2018-10-24 16:35
 */
public class NettyClientHandlerInitilizer extends ChannelInitializer<Channel> {


    @Override
    protected void initChannel(Channel socketChannel) {
        socketChannel.pipeline()
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                .addLast(new NettyClientHeartbeatHandler())
                .addLast(new NettyClientHandler());

    }
}
