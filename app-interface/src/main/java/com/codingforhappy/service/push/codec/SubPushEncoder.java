package com.codingforhappy.service.push.codec;

import com.codingforhappy.model.push.CustomMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class SubPushEncoder extends MessageToByteEncoder<CustomMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, CustomMessage msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getCommand_type());
        out.writeInt(msg.getAccount_type());
        out.writeBytes(msg.getMsg().getBytes(StandardCharsets.UTF_8));
    }
}
