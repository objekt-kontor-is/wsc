package de.objektkontor.wsc.container.http.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequestDecoder;
import de.objektkontor.wsc.container.InboundHandler;

public class RequestDecoder implements InboundHandler {

    @Override
    public String name() {
        return "HTTP Request Decoder";
    }

    @Override
    public Class<?> inputInboundType() {
        return ByteBuf.class;
    }

    @Override
    public Class<?> outputInboundType() {
        return HttpObject.class;
    }

    @Override
    public ChannelHandler create() {
        return new HttpRequestDecoder();
    }
}