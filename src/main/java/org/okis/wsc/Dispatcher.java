package org.okis.wsc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import org.okis.wsc.api.Error;
import org.okis.wsc.api.Handler;
import org.okis.wsc.api.Responder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dispatcher extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final static Logger log = LoggerFactory.getLogger(Dispatcher.class);

    private final Resolver resolver;

    public Dispatcher(Resolver resolver) {
		this.resolver = resolver;
	}

	@Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (!request.getDecoderResult().isSuccess())
            throw new Error(HttpResponseStatus.BAD_REQUEST);
        Handler handler = resolver.getHandler(request);
        if (handler == null)
            throw new Error(HttpResponseStatus.NOT_FOUND);
        handler.handleRequest(ctx, request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof Error) {
            log.error("Error handling request: " + cause);
            Responder.sendError(ctx, ((Error) cause).getCode(), cause.getMessage());
        } else {
            log.error("Error handling request", cause);
            Responder.sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, cause.getMessage());
        }
    }
}
