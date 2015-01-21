package org.okis.wsc;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Initializer extends ChannelInitializer<SocketChannel> {

    private final static String configBundle = "wsc";

    private static ExecutorService executorService;

    public static final String threadNamePrefix = "client-async-pool";
    public static final AtomicInteger threadCounter = new AtomicInteger(0);

    private final Resolver resolver;

    public Initializer(Resolver resolver) {
		this.resolver = resolver;
	}

	@Override
    public void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("httpDecoder", new HttpRequestDecoder());
        pipeline.addLast("httpEncoder", new HttpResponseEncoder());
        pipeline.addLast("httpAggregator", createHttpObjectAggregator());
        pipeline.addLast("dispatcher", new Dispatcher(resolver));
    }

    public static HttpObjectAggregator createHttpObjectAggregator() {
        int estimatedValueSize = getEstimatedValueSize();
        return new HttpObjectAggregator(estimatedValueSize);
    }

    public static ExecutorService getExecutorService() {
        synchronized (ExecutorService.class) {
            if (executorService == null)
                executorService = createExecutorService();
            return executorService;
        }
    }

    public static ExecutorService createExecutorService() {
        int threads = Configuration.getIntValue(configBundle, "clientThreads", 10);
        int queue = Configuration.getIntValue(configBundle, "clientQueueSize", 100);
        ;
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, threadNamePrefix + "-" + threadCounter.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        };
        return new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(queue), threadFactory);
    }

    public static Server createServer() {
        int port = Configuration.getIntValue(configBundle, "proxyPort", 8888);
        int socketBacklog = Configuration.getIntValue(configBundle, "socketBacklog", 8);
        boolean tcpNoDelay = Configuration.getBooleanValue(configBundle, "tcpNoDelay", true);
        boolean socketKeepAlive = Configuration.getBooleanValue(configBundle, "socketKeepAlive", true);
        return new Server(port, socketBacklog, tcpNoDelay, socketKeepAlive);
    }

    private static int getEstimatedValueSize() {
        return Configuration.getIntValue(configBundle, "estimatedValueSize", 1024 * 1024);
    }
}
