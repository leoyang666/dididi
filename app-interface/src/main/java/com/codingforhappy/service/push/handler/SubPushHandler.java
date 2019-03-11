package com.codingforhappy.service.push.handler;

import com.codingforhappy.model.push.CustomMessage;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import static com.codingforhappy.constants.AccountTypes.SERVER;
import static com.codingforhappy.constants.CommandTypes.MSG;
import static com.codingforhappy.constants.CommandTypes.PING;

public interface SubPushHandler {
    CustomMessage HEART_BEAT_RESPONSE = new CustomMessage(PING, SERVER, "pong");
    CustomMessage SUBSCRIBE_SUCCESS_RESPONSE = new CustomMessage(MSG, SERVER, "subscribe succeed");
    CustomMessage SUBSCRIBE_FAILED_RESPONSE = new CustomMessage(MSG, SERVER, "subscribe failed");

    /**
     * 尝试发送心跳响应，如果失败就关闭channel
     *
     * @param ctx
     */
    default void heartbeatResponse(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(HEART_BEAT_RESPONSE).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    default void subscribeSucceedResponse(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(SUBSCRIBE_SUCCESS_RESPONSE).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    default void subscribeFailedResponse(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(SUBSCRIBE_FAILED_RESPONSE).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }
}
