package de.objektkontor.wsc.container.proxy;

import io.netty.channel.EventLoopGroup;
import de.objektkontor.wsc.container.Pipeline;
import de.objektkontor.wsc.container.common.config.ServerConfig;
import de.objektkontor.wsc.container.core.AbstractEndpoint;

public class ProxyServer extends AbstractEndpoint<ServerConfig> {

    public static final String PROXY_HANDLER = "proxy";

    private final ProxyClient client;

    public ProxyServer(String id, ServerConfig config, EventLoopGroup group, ProxyClient client) {
        super(id, config, group);
        this.client = client;
    }

    @Override
    public Pipeline[] pipelines() {
        return new Pipeline[] { pipeline, client.pipeline() };
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
