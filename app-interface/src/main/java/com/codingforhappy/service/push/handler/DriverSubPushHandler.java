package com.codingforhappy.service.push.handler;

import com.codingforhappy.dao.redis.RedisService;
import com.codingforhappy.model.push.CustomMessage;
import com.codingforhappy.service.push.DriverChannelHolder;
import com.codingforhappy.token.JwtUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.codingforhappy.constants.AccountTypes.DRIVER;
import static com.codingforhappy.constants.CommandTypes.MSG;
import static com.codingforhappy.constants.CommandTypes.PING;

/**
 * 服务器的订阅、推送处理器
 */
@Component
@ChannelHandler.Sharable
public class DriverSubPushHandler extends SimpleChannelInboundHandler<CustomMessage> implements SubPushHandler {
    private final static Logger logger = LoggerFactory.getLogger(DriverSubPushHandler.class);

    @Autowired
    private RedisService redisService;

    //取消绑定
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        DriverChannelHolder.remove((NioSocketChannel) ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                logger.info("server: 已经30秒没有收到信息！");
                //已经15秒没有收到信息，尝试向客户端发送消息,如果发送失败就关闭channel
                heartbeatResponse(ctx);
            }
        }
        super.userEventTriggered(ctx, evt);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CustomMessage msg) throws Exception {
//        logger.info("收到msg={}", msg);
        switch (msg.getCommand_type()) {
            //收到客户端的心跳包
            case PING: {
//                logger.info("PING: msg={}", msg);
                //发送 pong 响应，发送失败就断开连接
                heartbeatResponse(ctx);
                break;
            }
            //收到客户端的订阅
            case MSG: {
                logger.info("MSG: msg.Msg={}", msg.getMsg());
                try {
                    String token = msg.getMsg();
                    //检查用户是否是登录状态
                    boolean logined = redisService.checkLogined(DRIVER, token);
                    if (!logined) {
                        subscribeFailedResponse(ctx);
                        break;
                    }

                    //用户已登录
                    String phoneNumber = (String) JwtUtils.getPhoneNumber(token);
                    DriverChannelHolder.put(phoneNumber, (NioSocketChannel) ctx.channel());
                    logger.info("司机={}，已订阅成功", phoneNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("something wrong");
                }
                subscribeSucceedResponse(ctx);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        DriverChannelHolder.remove((NioSocketChannel) ctx.channel());
        logger.error("exception caught!");
    }
}
