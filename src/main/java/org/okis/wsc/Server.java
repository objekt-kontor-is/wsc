package org.okis.wsc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.okis.wsc.api.URLTarget;
import org.okis.wsc.api.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Server implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(Server.class);

    private final int port;
    private final int socketBacklog;
    private final boolean tcpNoDelay;
    private final boolean socketKeepAlive;

    private final Resolver resolver = new Resolver();

    public Server(int port, int socketBacklog, boolean tcpNoDelay, boolean socketKeepAlive) {
        this.port = port;
        this.socketBacklog = socketBacklog;
        this.tcpNoDelay = tcpNoDelay;
        this.socketKeepAlive = socketKeepAlive;
    }

	public void registerHandler(URLTarget enpoint, Handler handler) {
    	resolver.registerHandler(enpoint, handler);
	}

	@Override
	public void run() {
        log.info("Starting server.");
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(eventLoopGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new Initializer(resolver));
            bootstrap.option(ChannelOption.SO_BACKLOG, socketBacklog);
            bootstrap.childOption(ChannelOption.TCP_NODELAY, tcpNoDelay);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, socketKeepAlive);
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            log.info("Accepting incomming connections on port: " + port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.info("Server has been interrupted.");
        } finally {
            eventLoopGroup.shutdownGracefully();
            log.info("Server stopped");
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = Initializer.createServer();
        server.run();
    }
}