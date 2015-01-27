package org.okis.wsc.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import org.okis.wsc.common.config.ClientConfig;

@Sharable
public class ProxyClient extends ChannelInboundHandlerAdapter {

    private final static AttributeKey<Channel> SOURCE_CHANNEL = AttributeKey.valueOf("sourceChannel");
    private final static AttributeKey<Channel> TARGET_CHANNEL = AttributeKey.valueOf("targetChannel");

    private final ClientConfig config;
    private final Bootstrap bootstrap;

    public ProxyClient(ClientConfig config) {
        this.config = config;
        bootstrap = createBootstrap();
    }

    public void group(EventLoopGroup group) {
        bootstrap.group(group);
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

    protected boolean needsFlush(Object message) {
        return true;
    }

    protected void handleError(ChannelHandlerContext context, Throwable cause, Channel sourceChannel) {
        sourceChannel.close();
    }

    @Override
    final public void channelRead(final ChannelHandlerContext context, final Object message) throws Exception {
        Channel sourceChannel = context.channel().attr(SOURCE_CHANNEL).get();
        if (sourceChannel != null) {
            receive(sourceChannel, message);
            return;
        }
        Channel targetChannel = context.channel().attr(TARGET_CHANNEL).get();
        if (targetChannel != null) {
            send(context, targetChannel, message);
            return;
        }
        bootstrap.connect(config.getHost(), config.getPort()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                final Channel sourceChannel = context.channel();
                final Channel targetChannel = future.channel();
                sourceChannel.attr(TARGET_CHANNEL).set(targetChannel);
                targetChannel.attr(SOURCE_CHANNEL).set(sourceChannel);
                targetChannel.pipeline().addLast("proxy", ProxyClient.this);
                sourceChannel.closeFuture().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        targetChannel.close();
                    }
                });
                targetChannel.closeFuture().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        sourceChannel.close();
                    }
                });
                if (future.isSuccess())
                    send(context, targetChannel, message);
                else
                    exceptionCaught(context, new ProxyError(future.cause()));
            }
        });
    }

    private void send(final ChannelHandlerContext context, Channel channel, Object message) {
        channel.write(message).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess())
                    exceptionCaught(context, new ProxyError(future.cause()));
            }
        });
        if (needsFlush(message))
            channel.flush();
    }

    private void receive(Channel channel, Object message) {
        channel.write(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        if (needsFlush(message))
            channel.flush();
    }

    @Override
    final public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        Channel sourceChannel = context.channel().attr(SOURCE_CHANNEL).get();
        if (sourceChannel == null)
            sourceChannel = context.channel();
        handleError(context, cause, sourceChannel);
    }
}
