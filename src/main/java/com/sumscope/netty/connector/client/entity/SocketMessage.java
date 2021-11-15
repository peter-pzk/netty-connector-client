package com.sumscope.netty.connector.client.entity;

/**
 * @author wuxiongwei
 * @date 2021/7/9 16:43
 * @Description
 */
public class SocketMessage {

    /**
     * 外层消息 head
     */
    private int outHead;

    /**
     * 内层消息 head
     */
    private int innerHead;

    /**
     * pb 消息对象名称
     */
    private String msgName;

    /**
     * pb 消息对象数组
     */
    private byte [] data;


    public int getInnerHead() {
        return innerHead;
    }

    public void setInnerHead(int innerHead) {
        this.innerHead = innerHead;
    }

    public int getOutHead() {
        return outHead;
    }

    public void setOutHead(int outHead) {
        this.outHead = outHead;
    }

    public String getMsgName() {
        return msgName;
    }

    public void setMsgName(String msgName) {
        this.msgName = msgName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
