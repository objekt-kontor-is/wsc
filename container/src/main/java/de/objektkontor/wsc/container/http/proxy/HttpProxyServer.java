package de.objektkontor.wsc.container.http.proxy;

import io.netty.channel.EventLoopGroup;
import de.objektkontor.wsc.container.Pipeline;
import de.objektkontor.wsc.container.http.config.HttpProxyConfig;
import de.objektkontor.wsc.container.http.handler.RequestAggregator;
import de.objektkontor.wsc.container.http.handler.RequestDecoder;
import de.objektkontor.wsc.container.http.handler.ResponseEncoder;
import de.objektkontor.wsc.container.proxy.ProxyClient;
import de.objektkontor.wsc.container.proxy.ProxyServer;

public class HttpProxyServer extends ProxyServer {

    protected final HttpProxyConfig config;

    public HttpProxyServer(String id, HttpProxyConfig config, EventLoopGroup group, ProxyClient client) {
        super(id, config.getServerConfig(), group, client);
        this.config = config;
    }

    @Override
    protected void buildServerPipeline(Pipeline pipeline) throws Exception {
        super.buildServerPipeline(pipeline);
        pipeline.addLast(new RequestDecoder());
        pipeline.addLast(new ResponseEncoder());
        pipeline.addLast(new RequestAggregator(config.getMaxContentLength()));
        pipeline.addLast(new HttpProxyHandler(config.getClientConfig()));
    }
}
