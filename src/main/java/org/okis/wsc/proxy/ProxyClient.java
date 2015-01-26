package org.okis.wsc.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import org.okis.wsc.api.Error;
import org.okis.wsc.config.common.ClientConfig;

public class ProxyClient {

    private final ClientConfig config;
    private final Bootstrap bootstrap;

    public ProxyClient(ClientConfig config) {
        this.config = config;
        bootstrap = createBootstrap();
    }

    private Bootstrap createBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, config.isTcpNoDelay());
        bootstrap.option(ChannelOption.SO_KEEPALIVE, config.isKeepAlive());
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                initPipeline(pipeline);
            }
        });
        return bootstrap;
    }

    protected void initPipeline(ChannelPipeline pipeline) {
    }

    public void group(EventLoopGroup group) {
        bootstrap.group(group);
    }

    public void send(final ProxyHandler handler, final ChannelHandlerContext context, final HttpRequest request) throws Exception {
        ChannelFuture connectFuture = bootstrap.connect(config.getHost(), config.getPort());
        connectFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                Channel clientChannel = future.channel();
                clientChannel.pipeline().addLast("proxy", handler);
                clientChannel.attr(ProxyHandler.SOURCE_CONTEXT).set(context);
                if (future.isSuccess())
                    clientChannel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) {
                            if (! future.isSuccess())
                                handler.exceptionCaught(context, new Error(HttpResponseStatus.BAD_GATEWAY, future.cause()));
                        }
                    });
                else
                    handler.exceptionCaught(context, new Error(HttpResponseStatus.BAD_GATEWAY, future.cause()));
            }
        });
    }
}
