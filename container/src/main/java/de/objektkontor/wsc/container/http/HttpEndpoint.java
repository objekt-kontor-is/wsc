package de.objektkontor.wsc.container.http;

import io.netty.channel.EventLoopGroup;
import de.objektkontor.wsc.container.Pipeline;
import de.objektkontor.wsc.container.core.AbstractEndpoint;
import de.objektkontor.wsc.container.http.config.HttpServerConfig;
import de.objektkontor.wsc.container.http.handler.RequestAggregator;
import de.objektkontor.wsc.container.http.handler.RequestDecoder;
import de.objektkontor.wsc.container.http.handler.ResponseEncoder;

public class HttpEndpoint extends AbstractEndpoint<HttpServerConfig> {

    public HttpEndpoint(String id, HttpServerConfig config, EventLoopGroup eventLoopGroup) {
        super(id, config, eventLoopGroup);
    }

    @Override
    protected Pipeline buildPipeline() throws Exception {
        Pipeline pipeline = super.buildPipeline();
        pipeline.addLast(new RequestDecoder());
        pipeline.addLast(new ResponseEncoder());
        pipeline.addLast(new RequestAggregator(config.getMaxContentLength()));
        return pipeline;
    }
}
