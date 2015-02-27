package de.objektkontor.wsc.container.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.objektkontor.wsc.container.Endpoint;
import de.objektkontor.wsc.container.Pipeline;
import de.objektkontor.wsc.container.common.config.ServerConfig;
import de.objektkontor.wsc.container.common.handler.TLSServerHandler;

public abstract class AbstractEndpoint<C extends ServerConfig> extends AbstractResource implements Endpoint {

    private final static Logger log = LoggerFactory.getLogger(AbstractEndpoint.class);

    protected final C config;
    protected final EventLoopGroup eventLoopGroup;
    protected Pipeline pipeline;
    protected ServerBootstrap bootstrap;
    protected ServerChannel channel;

    public AbstractEndpoint(String id, C config, EventLoopGroup eventLoopGroup) {
        super(Endpoint.class, id);
        this.config = config;
        this.eventLoopGroup = eventLoopGroup;
    }

    @Override
    public Connector<?>[] init() throws Exception {
        pipeline = buildPipeline();
        bootstrap = createBootstrap();
        return Connector.NO_CONNECTORS;
    }

    @Override
    public void start() throws Exception {
        super.start();
        channel = (ServerChannel) bootstrap.bind(port()).sync().channel();
        log.info(id() + ": using pipeline: " + pipeline);
        log.info(id() + ": listenning on port: " + port());
    }

    @Override
    public void stop() throws Exception {
        if (channel != null)
            channel.close().sync();
        super.stop();
    }

    @Override
    public void destroy() {
        pipeline = null;
        bootstrap = null;
        super.destroy();
    }

    @Override
    public int port() {
        return config.getPort();
    }

    @Override
    public Pipeline pipeline() {
        return pipeline;
    }

    @Override
    @Deprecated
    public Pipeline[] pipelines() {
        return new Pipeline[] { pipeline };
    }

    @Override
    public ChannelFuture closeFuture() {
        return channel.closeFuture();
    }

    protected void initBootstrap(ServerBootstrap bootstrap) {
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, config.getSocketBacklog());
        bootstrap.childOption(ChannelOption.TCP_NODELAY, config.isClientTcpNoDelay());
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, config.isClientKeepAlive());
    }

    protected Pipeline buildPipeline() throws Exception {
        Pipeline pipeline = new Pipeline("Server");
        pipeline.addFirst(new TLSServerHandler(config.getTlsConfig()));
        return pipeline;
    }

    protected void initChannelPipeline(ChannelPipeline channelPipeline) throws Exception {
        pipeline.init(channelPipeline);
    }

    private ServerBootstrap createBootstrap() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(eventLoopGroup);
        initBootstrap(bootstrap);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                initChannelPipeline(socketChannel.pipeline());
            }
        });
        return bootstrap;
    }

    protected void reconfigure(ServerBootstrap bootstrap) throws InterruptedException {
        // take server channel out of thread group
        channel.deregister().sync();

        // TODO: reconfigure bootstrap and server channel

        // reactivate server channel
        eventLoopGroup.register(channel).sync();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [port=" + port() + "]";
    }
}