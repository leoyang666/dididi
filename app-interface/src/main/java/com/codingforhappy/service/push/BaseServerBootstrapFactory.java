package com.codingforhappy.service.push;

import com.codingforhappy.service.push.codec.SubPushDecoder;
import com.codingforhappy.service.push.codec.SubPushEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

public class BaseServerBootstrapFactory {
    static ServerBootstrap getBootstrap(EventLoopGroup boss, EventLoopGroup work, int port, ChannelHandler subPushHandler) {
        return new ServerBootstrap()
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                //30秒没有收到消息 将IdleStateHandler 添加到 ChannelPipeline 中
                                .addLast(new IdleStateHandler(30, 0, 0))
                                .addLast(new SubPushDecoder())
                                .addLast(new SubPushEncoder())
                                .addLast(subPushHandler);
                    }
                });
        //保持长连接
    }
}
