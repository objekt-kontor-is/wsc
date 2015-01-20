package org.okis.wsc;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;


public class Initializer extends ChannelInitializer<SocketChannel> {

    public Initializer() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new ProxyHandler());
      
    }

    public static Server createServer() {
        int port = 11222;
        int socketBacklog = 8;
        boolean tcpNoDelay = true;
        boolean socketKeepAlive = true;
        return new Server(port, socketBacklog, tcpNoDelay, socketKeepAlive);
    }

}
