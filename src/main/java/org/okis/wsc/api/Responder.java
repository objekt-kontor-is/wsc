package org.okis.wsc.api;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class Responder {

    public static void sendResponse(ChannelHandlerContext context, HttpResponseStatus code) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, code);
        response.headers().set(Names.CONTENT_LENGTH, "0");
        context.writeAndFlush(response);
    }

    public static void sendResponse(ChannelHandlerContext context, String contentType, ByteBuf content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set(Names.CONTENT_TYPE, contentType);
        response.headers().set(Names.CONTENT_LENGTH, content.readableBytes());
        context.writeAndFlush(response);
    }

    public static void sendError(ChannelHandlerContext context, HttpResponseStatus code, String message) {
        if (context.channel().isActive()) {
            ByteBuf content = message == null ? Unpooled.EMPTY_BUFFER : Unpooled.copiedBuffer("Failure: " + code + ": " + message + "\n", CharsetUtil.UTF_8);
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, code, content);
            response.headers().set(Names.CONTENT_LENGTH, content.readableBytes());
            if (message != null)
                response.headers().set(Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
            context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
