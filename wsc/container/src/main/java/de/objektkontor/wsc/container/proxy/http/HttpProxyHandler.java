package de.objektkontor.wsc.container.proxy.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

import java.net.InetSocketAddress;

import de.objektkontor.wsc.container.common.config.ClientConfig;

public class HttpProxyHandler extends ChannelInboundHandlerAdapter {

    private final ClientConfig config;

    public HttpProxyHandler(ClientConfig config) {
        this.config = config;
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
