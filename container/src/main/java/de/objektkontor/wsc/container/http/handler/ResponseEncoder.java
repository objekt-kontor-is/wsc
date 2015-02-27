package de.objektkontor.wsc.container.http.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponseEncoder;
import de.objektkontor.wsc.container.OutboundHandler;

public class ResponseEncoder implements OutboundHandler {

    @Override
    public String name() {
        return "HTTP Response Encoder";
    }

    @Override
    public Class<?> inputOutboundType() {
        return HttpObject.class;
    }

    @Override
    public Class<?> outputOutboundType() {
        return ByteBuf.class;
    }

    @Override
    public ChannelHandler create() {
        return new HttpResponseEncoder();
    }
}