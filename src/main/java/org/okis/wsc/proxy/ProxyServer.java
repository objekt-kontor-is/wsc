package org.okis.wsc.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.okis.wsc.Container;
import org.okis.wsc.common.config.ServerConfig;

public class ProxyServer {

    private final ServerConfig config;
    private final ProxyClient client;
    private final ServerBootstrap bootstrap;

    public ProxyServer(ServerConfig config, ProxyClient client) {
        this.config = config;
        this.client = client;
        bootstrap = createBootstrap();
    }

    private ServerBootstrap createBootstrap() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, config.getSocketBacklog());
        bootstrap.childOption(ChannelOption.TCP_NODELAY, config.isClientTcpNoDelay());
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, config.isClientKeepAlive());
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                initPipeline(pipeline);
                pipeline.addLast("proxy", client);
            }
        });
        return bootstrap;
    }

    protected void initPipeline(ChannelPipeline pipeline) {
    }

    public void group(EventLoopGroup group) {
        bootstrap.group(group);
    }

    public void registerIn(Container container) throws InterruptedException {
        container.registerEndpoint(config.getPort(), bootstrap);
    }

    public void registerIn(Container container, EventLoopGroup eventLoopGroup) throws InterruptedException {
        if (eventLoopGroup != null)
            bootstrap.group(eventLoopGroup);
        container.registerEndpoint(config.getPort(), bootstrap);
    }
}
