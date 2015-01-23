package org.okis.wsc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ServerChannel;

public class Endpoint {

    private final int port;
    private final ServerBootstrap bootstrap;
    private ChannelFuture runtime;

    public Endpoint(int port, ServerBootstrap bootstrap) {
        this.port = port;
        this.bootstrap = bootstrap;
    }

    public void start() throws InterruptedException {
        runtime = bootstrap.bind(port).sync();
    }

    public void stop() throws InterruptedException {
        runtime.channel().close().sync();
    }

    public void reconfigure(ServerBootstrap bootstrap) throws InterruptedException {
        ServerChannel channel = (ServerChannel) runtime.channel();
        // take server channel out of thread group
        channel.deregister().sync();

        // TODO: reconfigure bootstrap and server channel

        // reactivate server channel
        bootstrap.group().register(channel).sync();
    }

    public ChannelFuture closeFuture() {
        return runtime.channel().closeFuture();
    }
}