package de.objektkontor.wsc.container.proxy;

import io.netty.channel.EventLoopGroup;
import de.objektkontor.wsc.container.Pipeline;
import de.objektkontor.wsc.container.common.config.ServerConfig;
import de.objektkontor.wsc.container.core.AbstractEndpoint;

public class ProxyServer<C extends ServerConfig> extends AbstractEndpoint<C> {

    public static final String PROXY_HANDLER = "proxy";

    private final ProxyClient client;

    public ProxyServer(String id, C config, EventLoopGroup group, ProxyClient client) {
        super(id, config, group);
        this.client = client;
    }

    protected void buildServerPipeline(Pipeline pipeline) throws Exception {
        client.buildPipeline();
    }

    @Override
    final protected Pipeline buildPipeline() throws Exception {
        Pipeline pipeline = super.buildPipeline();
        buildServerPipeline(pipeline);
        pipeline.addLast(client);
        return pipeline;
    }
}
