package de.objektkontor.wsc.container.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.objektkontor.config.ConfigComparator;
import de.objektkontor.config.ConfigObserver;
import de.objektkontor.config.ConfigUpdate;
import de.objektkontor.wsc.container.Endpoint;
import de.objektkontor.wsc.container.Pipeline;
import de.objektkontor.wsc.container.common.config.ServerConfig;
import de.objektkontor.wsc.container.common.handler.TLSServerHandler;

public abstract class AbstractEndpoint<C extends ServerConfig> extends AbstractResource implements Endpoint, ConfigObserver<C> {

    private final static Logger log = LoggerFactory.getLogger(AbstractEndpoint.class);

    protected final C config;
    protected volatile ServerBootstrap bootstrap;
    protected volatile ServerChannel channel;

    protected final EventLoopGroup eventLoopGroup;
    protected Pipeline pipeline;

    public AbstractEndpoint(String id, C config, EventLoopGroup eventLoopGroup) {
        super(Endpoint.class, id);
        this.eventLoopGroup = eventLoopGroup;
        this.config = config;
        this.config.setObserver(this);
    }

    @Override
    public Connector<?>[] init() throws Exception {
        pipeline = buildPipeline();
        bootstrap = createBootstrap(config);
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
    public void reconfigure(final C newConfig, List<ConfigUpdate> updates) {
        updates.add(new ConfigUpdate() {

            private ServerChannel newChannel = channel;
            private ServerBootstrap newBootstrap = bootstrap;
            private TLSServerHandler newTLSHandler = null;

            @Override
            public void prepare() throws Exception {
                // take server channel out of thread group
                channel.deregister().sync();

                if (needsNewBootstrap(newConfig))
                    newBootstrap = createBootstrap(newConfig);

                if (config.getPort() != newConfig.getPort())
                    newChannel = (ServerChannel) newBootstrap.bind(newConfig.getPort()).sync().channel();

                if (needsNewTLSHandler(newConfig))
                    newTLSHandler = new TLSServerHandler(newConfig.getTlsConfig());
            }

            @Override
            public void apply() {
                // apply new components and cleanup old ones
                if (bootstrap != newBootstrap)
                    bootstrap = newBootstrap;

                if (channel != newChannel) {
                    channel.close().syncUninterruptibly();
                    channel = newChannel;
                    log.info(id() + ": listenning on port: " + port());
                }

                if (newTLSHandler != null)
                    pipeline.replaceHandler(newTLSHandler);

                // reactivate server channel
                eventLoopGroup.register(channel);
            }

            @Override
            public void discard() {
                if (channel != newChannel)
                    newChannel.close().syncUninterruptibly();
                // reactivate server channel
                eventLoopGroup.register(channel).syncUninterruptibly();
            }
        });
    }

    protected Pipeline buildPipeline() throws Exception {
        Pipeline pipeline = new Pipeline("Server");
        pipeline.addFirst(new TLSServerHandler(config.getTlsConfig()));
        return pipeline;
    }

    private boolean needsNewBootstrap(C newConfig) {
        return config.getSocketBacklog() != newConfig.getSocketBacklog() ||
                config.isClientTcpNoDelay() != newConfig.isClientTcpNoDelay() ||
                config.isClientKeepAlive() != newConfig.isClientKeepAlive();
    }

    private ServerBootstrap createBootstrap(C config) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, config.getSocketBacklog());
        bootstrap.childOption(ChannelOption.TCP_NODELAY, config.isClientTcpNoDelay());
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, config.isClientKeepAlive());
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                pipeline.init(socketChannel.pipeline());
            }
        });
        return bootstrap;
    }

    private boolean needsNewTLSHandler(C newConfig) {
        return ! ConfigComparator.deepEquals(config.getTlsConfig(), newConfig.getTlsConfig());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [port=" + port() + "]";
    }
}