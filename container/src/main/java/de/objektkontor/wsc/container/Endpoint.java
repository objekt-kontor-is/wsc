package de.objektkontor.wsc.container;

import io.netty.channel.ChannelFuture;

public interface Endpoint extends Resource {

    public abstract int port();

    public abstract Pipeline pipeline();

    @Deprecated
    public abstract Pipeline[] pipelines();

    public abstract ChannelFuture closeFuture();

}
