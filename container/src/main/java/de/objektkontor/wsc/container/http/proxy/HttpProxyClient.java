package de.objektkontor.wsc.container.http.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.HttpResponseStatus;
import de.objektkontor.wsc.container.Pipeline;
import de.objektkontor.wsc.container.http.HttpError;
import de.objektkontor.wsc.container.http.HttpResponder;
import de.objektkontor.wsc.container.http.config.HttpProxyConfig;
import de.objektkontor.wsc.container.http.handler.RequestAggregator;
import de.objektkontor.wsc.container.http.handler.RequestEncoder;
import de.objektkontor.wsc.container.http.handler.ResponseDecoder;
import de.objektkontor.wsc.container.proxy.ProxyClient;
import de.objektkontor.wsc.container.proxy.ProxyError;

public class HttpProxyClient extends ProxyClient {

    protected final HttpProxyConfig config;

    public HttpProxyClient(HttpProxyConfig config, EventLoopGroup group) {
        super(config.getClientConfig(), group);
        this.config = config;
    }

    @Override
    protected void buildClientPipeline(Pipeline pipeline) {
        pipeline.addLast(new ResponseDecoder());
        pipeline.addLast(new RequestEncoder());
        pipeline.addLast(new RequestAggregator(config.getMaxContentLength()));
    }

    @Override
    protected void handleError(ChannelHandlerContext context, Throwable cause, Channel sourceChannel) {
        if (cause instanceof HttpError)
            HttpResponder.sendError(sourceChannel, ((HttpError) cause).getCode(), cause.getMessage());
        else if (cause instanceof ProxyError)
            HttpResponder.sendError(sourceChannel, HttpResponseStatus.BAD_GATEWAY, cause.getMessage());
        else
            HttpResponder.sendError(sourceChannel, HttpResponseStatus.INTERNAL_SERVER_ERROR, cause.getMessage());
    }
}
