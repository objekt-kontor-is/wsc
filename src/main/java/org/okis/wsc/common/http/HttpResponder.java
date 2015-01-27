package org.okis.wsc.common.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpResponder {

    public static ChannelFuture sendResponse(ChannelHandlerContext context, HttpResponseStatus code) {
        return sendResponse(context.channel(), code);
    }

    public static ChannelFuture sendResponse(Channel channel, HttpResponseStatus code) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, code);
        response.headers().set(Names.CONTENT_LENGTH, "0");
        return channel.writeAndFlush(response);
    }

    public static ChannelFuture sendResponse(ChannelHandlerContext context, String contentType, ByteBuf content) {
        return sendResponse(context.channel(), contentType, content);
    }

    public static ChannelFuture sendResponse(Channel channel, String contentType, ByteBuf content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set(Names.CONTENT_TYPE, contentType);
        response.headers().set(Names.CONTENT_LENGTH, content.readableBytes());
        return channel.writeAndFlush(response);
    }

    public static void sendError(ChannelHandlerContext context, HttpResponseStatus code, String message) {
        sendError(context.channel(), code, message);
    }

    public static void sendError(Channel channel, HttpResponseStatus code, String message) {
        if (channel.isActive()) {
            ByteBuf content = message == null ? Unpooled.EMPTY_BUFFER : Unpooled.copiedBuffer("Failure: " + code + ": " + message + "\n", CharsetUtil.UTF_8);
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, code, content);
            response.headers().set(Names.CONTENT_LENGTH, content.readableBytes());
            if (message != null)
                response.headers().set(Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
            channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
