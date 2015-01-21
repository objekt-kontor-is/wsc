package org.okis.wsc.api;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface Handler {

	public final static String ENDPOINT_PROPERTY = "Endpoint";

	void handleRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Error;
}
