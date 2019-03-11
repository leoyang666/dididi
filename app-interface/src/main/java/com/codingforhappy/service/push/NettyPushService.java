package com.codingforhappy.service.push;

import com.codingforhappy.model.push.CustomMessage;
import com.codingforhappy.service.push.handler.DriverSubPushHandler;
import com.codingforhappy.service.push.handler.PassengerSubPushHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static com.codingforhappy.constants.AccountTypes.*;
import static com.codingforhappy.constants.CommandTypes.MSG;
import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;

@Service
public class NettyPushService {
    private final static Logger logger = LoggerFactory.getLogger(NettyPushService.class);
    //netty server监听的本地端口
    private static final int passenger_port = 7000;
    private static final int driver_port = 7001;
    private DriverSubPushHandler driverSubPushHandler;
    private PassengerSubPushHandler passengerSubPushHandler;
    private EventLoopGroup driverBoss = new NioEventLoopGroup();
    private EventLoopGroup driverWork = new NioEventLoopGroup();
    private EventLoopGroup passengerBoss = new NioEventLoopGroup();
    private EventLoopGroup passengerWork = new NioEventLoopGroup();
    @Autowired
    public NettyPushService(DriverSubPushHandler driverSubPushHandler, PassengerSubPushHandler passengerSubPushHandler) {
        this.driverSubPushHandler = driverSubPushHandler;
        this.passengerSubPushHandler = passengerSubPushHandler;
    }

    @PostConstruct
    public void start() throws InterruptedException {

        ServerBootstrap driverBootstrap =
                BaseServerBootstrapFactory.getBootstrap(driverBoss, driverWork, driver_port, driverSubPushHandler);

        ServerBootstrap passengerBootstrap =
                BaseServerBootstrapFactory.getBootstrap(passengerBoss, passengerWork, passenger_port, passengerSubPushHandler);

        ChannelFuture driverFuture = driverBootstrap.bind().sync();
        ChannelFuture passengerFuture = passengerBootstrap.bind().sync();
        if (driverFuture.isSuccess() && passengerFuture.isSuccess()) {
            logger.info("启动 Netty Server  成功");
        }

    }

    @PreDestroy
    public void destroy() {
        driverBoss.shutdownGracefully().syncUninterruptibly();
        driverWork.shutdownGracefully().syncUninterruptibly();
        passengerBoss.shutdownGracefully().syncUninterruptibly();
        passengerWork.shutdownGracefully().syncUninterruptibly();
        logger.info("关闭  Netty Server 成功");
    }

    /*  -------------------------------以下是对外提供的司机服务------------------------------------   */

    /**
     * 乘客和司机握手的过程中，发送给司机的信息，CustomMessage.account_type为PASSENGER
     * @param phoneNumber 司机手机号
     * @param body        发送给客户端的信息的body
     * @return true推送成功，false推送失败
     */
    public boolean sendToSubscribedDriver(String phoneNumber, String body) {
        NioSocketChannel channel = DriverChannelHolder.get(phoneNumber);
        if (channel != null) {
            CustomMessage message = new CustomMessage(MSG, PASSENGER, body);
            logger.info("获取到司机={}的channel，准备发送数据={}", phoneNumber, message);
            channel.writeAndFlush(message).addListener(CLOSE_ON_FAILURE);
            return true;
        } else
            return false;
    }

    /**
     * 检查是否当前的用户已订阅了推送服务
     */
    public boolean checkDriverSubscribed(String phoneNumber) {
        return DriverChannelHolder.contains(phoneNumber);
    }

    public void driverUnsubscribe(String phoneNumber) {
        NioSocketChannel channel = DriverChannelHolder.get(phoneNumber);
        if (channel != null) {
            DriverChannelHolder.remove(phoneNumber);
            channel.close();
        }
    }

    /*  -------------------------------以下是对外提供的乘客服务------------------------------------   */

    /**
     * 乘客和司机握手的过程中，发送给乘客的信息，CustomMessage.account_type为DRIVER
     * @param phoneNumber 乘客手机号
     * @param body        发送给客户端的信息的body
     * @return true推送成功，false推送失败
     */
    public boolean sendToSubscribedPassenger(String phoneNumber, String body) {
        NioSocketChannel channel = PassengerChannelHolder.get(phoneNumber);
        if (channel != null) {
            CustomMessage message = new CustomMessage(MSG, DRIVER, body);
            logger.info("获取到乘客={}的channel，准备发送数据={}", phoneNumber, message);
            channel.writeAndFlush(message).addListener(CLOSE_ON_FAILURE);
            return true;
        } else
            return false;
    }

    public boolean checkPassengerSubscribed(String phoneNumber) {
        return PassengerChannelHolder.contains(phoneNumber);
    }

    public void passengerUnsubscribe(String phoneNumber) {
        NioSocketChannel channel = PassengerChannelHolder.get(phoneNumber);
        if (channel != null) {
            PassengerChannelHolder.remove(phoneNumber);
            channel.close();
        }
    }


}
