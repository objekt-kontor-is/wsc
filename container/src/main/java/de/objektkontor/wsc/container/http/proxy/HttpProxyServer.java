package de.objektkontor.wsc.container.http.proxy;

import io.netty.channel.EventLoopGroup;

import java.util.List;

import de.objektkontor.config.ConfigComparator;
import de.objektkontor.config.ConfigObserver;
import de.objektkontor.config.ConfigUpdate;
import de.objektkontor.wsc.container.Pipeline;
import de.objektkontor.wsc.container.http.config.HttpProxyConfig;
import de.objektkontor.wsc.container.http.config.HttpServerConfig;
import de.objektkontor.wsc.container.http.handler.RequestAggregator;
import de.objektkontor.wsc.container.http.handler.RequestDecoder;
import de.objektkontor.wsc.container.http.handler.ResponseEncoder;
import de.objektkontor.wsc.container.proxy.ProxyClient;
import de.objektkontor.wsc.container.proxy.ProxyServer;

public class HttpProxyServer extends ProxyServer<HttpServerConfig> {

    protected final HttpProxyConfig config;

    public HttpProxyServer(String id, HttpProxyConfig config, EventLoopGroup group, ProxyClient client) {
        super(id, config.getServerConfig(), group, client);
        this.config = config;
        this.config.setObserver(new ProxyConfigObserver());
    }

    @Override
    protected void buildServerPipeline(Pipeline pipeline) throws Exception {
        super.buildServerPipeline(pipeline);
        pipeline.addLast(new RequestDecoder());
        pipeline.addLast(new ResponseEncoder());
        pipeline.addLast(new RequestAggregator(config.getMaxContentLength()));
        pipeline.addLast(new HttpProxyHandler(config.getClientConfig()));
    }

    private class ProxyConfigObserver implements ConfigObserver<HttpProxyConfig> {

        @Override
        public void reconfigure(final HttpProxyConfig newConfig, List<ConfigUpdate> updates) {
            updates.add(new ConfigUpdate() {

                private RequestAggregator newRequestAggregator = null;
                private HttpProxyHandler newProxyHandler = null;

                @Override
                public void prepare() throws Exception {
                    if (config.getMaxContentLength() != newConfig.getMaxContentLength())
                        newRequestAggregator = new RequestAggregator(newConfig.getMaxContentLength());

                    if (! ConfigComparator.deepEquals(config.getClientConfig(), newConfig.getClientConfig()))
                        newProxyHandler = new HttpProxyHandler(newConfig.getClientConfig());
                }

                @Override
                public void apply() {
                    if (newRequestAggregator != null)
                        pipeline.replaceHandler(newRequestAggregator);

                    if (newProxyHandler != null)
                        pipeline.replaceHandler(newProxyHandler);
                }

                @Override
                public void discard() {
                }
            });
        }
    }
}
