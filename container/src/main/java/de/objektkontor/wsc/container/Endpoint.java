package de.objektkontor.wsc.container;

import io.netty.channel.ChannelFuture;


public interface Endpoint {

    public abstract int getPort();

    public abstract void start() throws InterruptedException;

    public abstract void stop() throws InterruptedException;

    public abstract ChannelFuture closeFuture();
}
