package org.okis.wsc.proxy;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AttributeKey;

import org.okis.wsc.api.Error;
import org.okis.wsc.api.Responder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class ProxyHandler extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(ProxyHandler.class);

    public final static AttributeKey<ChannelHandlerContext> SOURCE_CONTEXT = AttributeKey.valueOf("sourceContext");

    private final ProxyClient proxyClient;

    public ProxyHandler(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest)
            handleSourceRequest(context, (FullHttpRequest) msg);
        else if (msg instanceof FullHttpResponse)
            handleTargetResponse(context, (FullHttpResponse) msg);
        else
            context.fireChannelRead(msg);
    }

    private void handleSourceRequest(ChannelHandlerContext sourceContext, FullHttpRequest request) throws Exception {
        if (!request.getDecoderResult().isSuccess())
            throw new Error(HttpResponseStatus.BAD_REQUEST);
        proxyClient.send(this, sourceContext, request);
    }

    private void handleTargetResponse(ChannelHandlerContext targetContext, FullHttpResponse response) throws Exception {
        if (!response.getDecoderResult().isSuccess())
            throw new Error(HttpResponseStatus.BAD_GATEWAY);
        ChannelHandlerContext sourceContext = targetContext.channel().attr(SOURCE_CONTEXT).get();
        sourceContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        log.error("Error handling request: " + cause);
        ChannelHandlerContext sourceContext = context.channel().attr(SOURCE_CONTEXT).get();
        boolean clientError = sourceContext != null;
        sourceContext = clientError ? sourceContext : context;
        if (cause instanceof Error)
            Responder.sendError(sourceContext, ((Error) cause).getCode(), cause.getMessage());
        else
            Responder.sendError(sourceContext, clientError ? HttpResponseStatus.BAD_GATEWAY : HttpResponseStatus.INTERNAL_SERVER_ERROR, cause.getMessage());
    }
}

