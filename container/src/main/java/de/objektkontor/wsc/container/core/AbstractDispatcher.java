package de.objektkontor.wsc.container.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.PipelineMultiplexerChannel;
import io.netty.util.AttributeKey;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.objektkontor.wsc.container.Dispatcher;
import de.objektkontor.wsc.container.Endpoint;
import de.objektkontor.wsc.container.Handler;
import de.objektkontor.wsc.container.InboundHandler;
import de.objektkontor.wsc.container.Pipeline;
import de.objektkontor.wsc.container.Processor;
import de.objektkontor.wsc.container.Selector;

public abstract class AbstractDispatcher<K, M, S extends Selector> extends AbstractResource implements Dispatcher<K, M, S> {

    private final static String DISPATCHER_HANDLER_NAME = "Dispatcher";

    private final static AttributeKey<Map<Object, PipelineMultiplexerChannel>> DISPATCHER_CHANNEL_CACHE = AttributeKey.valueOf("pipelineCache");

    private final ConcurrentMap<K, Pipeline> pipelineCache = new ConcurrentHashMap<>();
    private Handler defaultHandler;

    private int connectedEndpoints = 0;
    private int connectedProcessors = 0;

    public AbstractDispatcher(String id) {
        super(Dispatcher.class, id);
    }

    protected abstract void addProcessor(Processor<S> processor) throws Exception;

    protected abstract void removeProcessor(Processor<S> processor) throws Exception;

    @Override
    public Connector<?>[] init() throws Exception {
        defaultHandler = defaultHadler();
        return new Connector<?> [] {
                new Connector<Endpoint>() {

                    @Override
                    public Class<Endpoint> type() {
                        return Endpoint.class;
                    }

                    @Override
                    public void connect(Endpoint endpoint) {
                        endpoint.pipeline().addLast(new DispatcherInboundHandler());
                        if (defaultHandler != null)
                            endpoint.pipeline().addLast(defaultHandler);
                        connectedEndpoints ++;
                    }

                    @Override
                    public void disconnect(Endpoint endpoint) {
                        endpoint.pipeline().removeHandler(DISPATCHER_HANDLER_NAME);
                        if (defaultHandler != null)
                            endpoint.pipeline().removeHandler(defaultHandler.name());
                        connectedEndpoints --;
                    }
                },

                new Connector<Processor<S>>() {

                    @Override
                    @SuppressWarnings({ "rawtypes", "unchecked" })
                    public Class<Processor<S>> type() {
                        return (Class) Processor.class;
                    }

                    @Override
                    public void connect(Processor<S> processor) throws Exception {
                        addProcessor(processor);
                        connectedProcessors ++;
                    }

                    @Override
                    public void disconnect(Processor<S> processor) throws Exception {
                        removeProcessor(processor);
                        connectedProcessors --;
                    }
                }
        };
    }

    @Override
    public boolean ready() {
        return connectedEndpoints > 0 && connectedProcessors > 0;
    }

    @Override
    public void destroy() {
        defaultHandler = null;
        super.destroy();
    }

    @Override
    public Handler defaultHadler() {
        return null;
    }

    @Sharable
    private class DispatcherInboundHandler extends ChannelInboundHandlerAdapter implements InboundHandler {

        @Override
        public String name() {
            return DISPATCHER_HANDLER_NAME;
        }

        @Override
        public Class<?> inputInboundType() {
            return Object.class;
        }

        @Override
        public Class<?> outputInboundType() {
            return defaultHandler == null ? null : Object.class;
        }

        @Override
        public ChannelHandler create() {
            return this;
        }

        @SuppressWarnings("unchecked")
        private PipelineMultiplexerChannel getPipelineChannel(ChannelHandlerContext context, Object message) throws Exception {
            K key = identify((M) message);
            if (key == null)
                return null;
            Channel origin = context.channel();
            Map<Object, PipelineMultiplexerChannel> channels = origin.attr(DISPATCHER_CHANNEL_CACHE).get();
            PipelineMultiplexerChannel channel = channels.get(key);
            if (channel == null) {
                Pipeline pipeline = pipelineCache.get(key);
                if (pipeline == null) {
                    pipeline = dispatch(key);
                    if (pipeline == null)
                        return null;
                    pipelineCache.put(key, pipeline);
                }
                channel = new PipelineMultiplexerChannel(origin);
                ChannelPipeline channelPipeline = channel.pipeline();
                pipeline.init(channelPipeline);
                channelPipeline.addFirst(DISPATCHER_HANDLER_NAME, new DispatcherOutboundHandler(channel));
                channels.put(key, channel);
            }
            return channel;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext context) throws Exception {
            context.channel().attr(DISPATCHER_CHANNEL_CACHE).set(new HashMap<Object, PipelineMultiplexerChannel>());
        }

        @Override
        public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
            PipelineMultiplexerChannel channel = getPipelineChannel(context, message);
            if (channel != null)
                channel.pipeline().fireChannelRead(message);
            else
                super.channelRead(context, message);
        }
    }

    private static class DispatcherOutboundHandler extends ChannelOutboundHandlerAdapter {

        private final PipelineMultiplexerChannel channel;

        public DispatcherOutboundHandler(PipelineMultiplexerChannel channel) {
            this.channel = channel;
        };

        @Override
        public void write(ChannelHandlerContext context, Object message, ChannelPromise promise) throws Exception {
            channel.write(message, promise);
        }

        @Override
        public void flush(ChannelHandlerContext context) throws Exception {
            channel.flush();
        }
    }
}
