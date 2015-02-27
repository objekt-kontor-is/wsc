package de.objektkontor.wsc.container;

import io.netty.channel.ChannelHandler;

public interface Handler {

    public abstract String name();

    public abstract ChannelHandler create();

}
