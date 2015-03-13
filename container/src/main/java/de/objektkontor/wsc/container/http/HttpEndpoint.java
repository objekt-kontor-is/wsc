package de.objektkontor.wsc.container.http;

import io.netty.channel.EventLoopGroup;

import java.util.List;

import de.objektkontor.config.ConfigUpdate;
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

    @Override
    public void reconfigure(final HttpServerConfig newConfig, List<ConfigUpdate> updates) {
        super.reconfigure(newConfig, updates);
        updates.add(new ConfigUpdate() {

            private RequestAggregator newRequestAggregator = null;

            @Override
            public void prepare() throws Exception {
                if (config.getMaxContentLength() != newConfig.getMaxContentLength())
                    newRequestAggregator = new RequestAggregator(newConfig.getMaxContentLength());
            }

            @Override
            public void apply() {
                if (newRequestAggregator != null)
                    pipeline.replaceHandler(newRequestAggregator);
            }

            @Override
            public void discard() {
            }
        });
    }
}
