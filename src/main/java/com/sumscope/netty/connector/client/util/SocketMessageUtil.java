package com.sumscope.netty.connector.client.util;

import com.sumscope.netty.connector.client.entity.SocketMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author peter-pan
 * @date 2021/11/14
 * @Description 发送/接收 c++ 消息编码解码工具类
 */
@Component
public class SocketMessageUtil {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * 将传输的数据按格式进行编码
     *
     * @param bytes   pb 对象序列化字节数组
     * @param msgName pb 对象名称
     * @return 用于传输的字节对象
     */
    public static ByteBuf encode(byte[] bytes, String msgName) {
        byte[] msgNameBytes = msgName.getBytes();

        int netTotalSize = 4 + 4 + bytes.length + msgNameBytes.length;
        ByteBuffer net_data = ByteBuffer.allocate(netTotalSize);
        //java 默认是 BIG_ENDIAN ，转换为 LITTLE_ENDIAN
        net_data.order(ByteOrder.LITTLE_ENDIAN);
        // net head 长度
        int innerHead = bytes.length << 8 | msgNameBytes.length;
        // pb head 长度
        int outHead = msgNameBytes.length + bytes.length + 4;
        net_data.putInt(outHead);
        net_data.putInt(innerHead);
        net_data.put(msgNameBytes);
        net_data.put(bytes);

        byte[] array = net_data.array();

        return Unpooled.copiedBuffer(array);
    }


    /**
     * 将传输的数据按格式进行编码（此方法用于传输心跳）
     * net head = 0 , 没有 net data部分的，视为心跳
     *
     * @return 用于传输的字节对象
     */
    public static ByteBuf encodeHeartBeat() {
        ByteBuffer net_data = ByteBuffer.allocate(4);
        //java 默认是 BIG_ENDIAN ，转换为 LITTLE_ENDIAN
        net_data.order(ByteOrder.LITTLE_ENDIAN);
        // net head 长度
        int outHead = 0;
        net_data.putInt(outHead);
        byte[] array = net_data.array();

        return Unpooled.copiedBuffer(array);
    }


    /**
     * 将接收到 c++ 的字节数组转换为消息对象
     *
     * @param bytes 接收到 c++ 的字节数组
     * @return 返回消息对象
     */
    public static SocketMessage decode(byte[] bytes) {
        SocketMessage socketMessage = new SocketMessage();

        //反解析实体
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(bytes);

        //读取
        byteBuffer.flip();

        int reOutHead = byteBuffer.getInt();
        socketMessage.setOutHead(reOutHead);

        int reInnerHead = byteBuffer.getInt();
        socketMessage.setInnerHead(reInnerHead);

        int nameSize = (byte) reInnerHead;
        int dataSize = reInnerHead >> 8;

        // pb 消息对象名称
        byte[] name = new byte[nameSize];
        byteBuffer.get(name);
        socketMessage.setMsgName(new String(name));

        // pb 消息对象内容
        byte[] data = new byte[dataSize];
        byteBuffer.get(data);
        socketMessage.setData(data);

        return socketMessage;
    }
}
