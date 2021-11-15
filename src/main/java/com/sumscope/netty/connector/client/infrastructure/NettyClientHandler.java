package com.sumscope.netty.connector.client.infrastructure;


import com.sumscope.netty.connector.client.entity.SocketMessage;
import com.sumscope.netty.connector.client.protobuf.SubscribeReq;
import com.sumscope.netty.connector.client.protobuf.SubscribeResp;
import com.sumscope.netty.connector.client.util.SocketMessageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * @author peter-pan
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * 如果服务端发送消息给客户端，下面方法进行接收消息
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("client received message loading.....");
        ByteBuf msg1 = (ByteBuf) msg;
        byte[] array = new byte[msg1.readableBytes()];
        int readerIndex = msg1.readerIndex();
        msg1.getBytes(readerIndex, array);
        //释放
        ReferenceCountUtil.release(msg1);
        // 解析字节数组
        SocketMessage socketMessage = SocketMessageUtil.decode(array);
        if("SubscribeResp.BankMtkRequestResp".equals(socketMessage.getMsgName())){
            SubscribeResp.BankMtkRequestResp bankMtkRequestResp = SubscribeResp.BankMtkRequestResp.parseFrom(socketMessage.getData());
            logger.info("client received pb  BankMtkRequestResp:{}",bankMtkRequestResp);
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        SubscribeReq.BankMtkRequestReq.Builder builder = SubscribeReq.BankMtkRequestReq.newBuilder();
        SubscribeReq.BankMtkRequestReq bankMtkRequestReq = builder.setAccountCode("XT123")
                .setCombiNo("cb")
                .setInvestType("a")
                .setRequestId("10086")
                .setOwnerOperatorName("pzk")
                .setNetPrice(45.33)
                .setFaceBalance(283456)
                .setValidTime(20211231)
                .build();
        logger.info("client sending message:{}", bankMtkRequestReq);
        ctx.writeAndFlush(SocketMessageUtil.encode(bankMtkRequestReq.toByteArray(), "SubscribeReq.BankMtkRequestReq"));
    }

}
