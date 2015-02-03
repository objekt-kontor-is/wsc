package de.objektkontor.wsc.container.common;

import de.objektkontor.wsc.container.Endpoint;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ServerChannel;

public abstract class Server implements Endpoint {

    protected ServerBootstrap bootstrap;
    protected ServerChannel channel;

    public abstract ServerBootstrap getBootstrap();

    @Override
    public void start() throws InterruptedException {
        bootstrap = getBootstrap();
        channel = (ServerChannel) bootstrap.bind(getPort()).sync().channel();
    }

    @Override
    public void stop() throws InterruptedException {
        channel.close().sync();
        bootstrap.group().shutdownGracefully().sync();
    }

    @Override
    public ChannelFuture closeFuture() {
        return channel.closeFuture();
    }

    protected void reconfigure(ServerBootstrap bootstrap) throws InterruptedException {
        // take server channel out of thread group
        channel.deregister().sync();

        // TODO: reconfigure bootstrap and server channel

        // reactivate server channel
        bootstrap.group().register(channel).sync();
    }
}