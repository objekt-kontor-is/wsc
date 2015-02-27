package de.objektkontor.wsc.container.http.handler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import de.objektkontor.wsc.container.InboundHandler;

public class RequestAggregator implements InboundHandler {

    private final int maxContentLength;

    public RequestAggregator(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    @Override
    public String name() {
        return "HTTP Aggregator";
    }

    @Override
    public Class<?> inputInboundType() {
        return HttpObject.class;
    }

    @Override
    public Class<?> outputInboundType() {
        return FullHttpRequest.class;
    }

    @Override
    public ChannelHandler create() {
        return maxContentLength > 0 ? new HttpObjectAggregator(maxContentLength) : null;
    }
}