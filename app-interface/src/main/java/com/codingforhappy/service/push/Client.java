package com.codingforhappy.service.push;

import com.codingforhappy.service.push.codec.SubPushDecoder;
import com.codingforhappy.service.push.codec.SubPushEncoder;
import com.codingforhappy.service.push.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private static final String host = "127.0.0.1";
    private static final int port = 7001;

    public static int user_id = 0;

    public static void main(String[] args) throws Exception {
        if (args.length > 0)
            user_id = Integer.parseInt(args[0]);
        System.out.println("user_id:" + user_id);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            logger.info("准备bootstrap");
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<Channel>() {
                                 @Override
                                 protected void initChannel(Channel ch) throws Exception {
                                     ch.pipeline()
                                             .addLast(new IdleStateHandler(0, 10, 0))
                                             .addLast(new SubPushDecoder())
                                             .addLast(new SubPushEncoder())
                                             .addLast(new ClientHandler());
                                 }
                             }
                    );

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}

