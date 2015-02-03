package de.objektkontor.wsc.container.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import de.objektkontor.wsc.container.common.Server;
import de.objektkontor.wsc.container.common.config.ServerConfig;

public abstract class ProxyServer extends Server {

    private final ServerConfig config;
    private final ProxyClient client;
    private final EventLoopGroup group;

    public ProxyServer(ServerConfig config, EventLoopGroup group, ProxyClient client) {
        this.config = config;
        this.group = group;
        this.client = client;
    }

    @Override
    public ServerBootstrap getBootstrap() {
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
        bootstrap.group(group);
        return bootstrap;
    }

    protected void initPipeline(ChannelPipeline pipeline) {
    }
}
