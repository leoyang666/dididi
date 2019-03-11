package com.codingforhappy.service.push.handler;

import com.codingforhappy.model.push.CustomMessage;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codingforhappy.constants.AccountTypes.PASSENGER;
import static com.codingforhappy.constants.CommandTypes.MSG;
import static com.codingforhappy.constants.CommandTypes.PING;

public class ClientHandler extends SimpleChannelInboundHandler<CustomMessage> {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private static final String TEST_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHQiOjE1NTE4NjMxMzYxNDQsInBob25lTnVtYmVyIjoiMTMwMTIzMTAwMDAifQ.RQUrjS_zEjklLz6Ai88V5Rye8vpv-ZJzSPHpVIUxYeA";


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        CustomMessage msg_test_passenger = new CustomMessage(MSG, PASSENGER, TEST_TOKEN);
        ctx.writeAndFlush(msg_test_passenger).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        super.channelActive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
//                logger.info("已经过去10秒，准备发送心跳包！");
                //每10秒向服务端发送心跳包，如果发送失败，断开连接
                CustomMessage heartBeat = new CustomMessage(PING, PASSENGER, "ping");
                ctx.writeAndFlush(heartBeat).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CustomMessage msg) throws Exception {
        int command_type = msg.getCommand_type();
        //心跳包不写入log
        if (command_type == MSG)
            logger.info("客户端收到消息 msg={}", msg);
    }
}
