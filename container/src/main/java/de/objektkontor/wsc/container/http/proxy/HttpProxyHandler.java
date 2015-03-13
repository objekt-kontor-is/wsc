package de.objektkontor.wsc.container.http.proxy;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;

import java.net.InetSocketAddress;

import de.objektkontor.wsc.container.InboundHandler;
import de.objektkontor.wsc.container.common.config.ClientConfig;

@Sharable
public class HttpProxyHandler extends ChannelInboundHandlerAdapter implements InboundHandler {

    private final ClientConfig config;

    public HttpProxyHandler(ClientConfig config) {
        this.config = config;
    }

    @Override
    public String name() {
        return "Proxy Headers";
    }

    @Override
    public Class<?> inputInboundType() {
        return HttpMessage.class;
    }

    @Override
    public Class<?> outputInboundType() {
        return HttpMessage.class;
    }

    @Override
    public ChannelHandler create() {
        return this;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        if (msg instanceof HttpRequest)
            handleRequest(context, (HttpRequest) msg);
        context.fireChannelRead(msg);
    }

    private void handleRequest(ChannelHandlerContext context, HttpRequest request) {
        HttpHeaders headers = request.headers();
        headers.set("Host", config.getHost());
        String clientAddress = request.headers().get("X-Forwarded-For");
        if (clientAddress == null) {
            clientAddress = ((InetSocketAddress) context.channel().remoteAddress()).getHostString();
            headers.set("X-Forwarded-For", clientAddress);
        }
    }
}
