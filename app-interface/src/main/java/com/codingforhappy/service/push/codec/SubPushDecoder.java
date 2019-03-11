package com.codingforhappy.service.push.codec;

import com.codingforhappy.model.push.CustomMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class SubPushDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int command = in.readInt();
        int account_type = in.readInt();
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        String token = new String(bytes);
        CustomMessage message = new CustomMessage(command, account_type, token);
        out.add(message);
    }
}
