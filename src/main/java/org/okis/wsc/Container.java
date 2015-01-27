package org.okis.wsc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;

import java.util.HashMap;
import java.util.Map;

import org.okis.wsc.common.Endpoint;

public class Container {

    private final EventLoopGroup defaultEventLoopGroup;

    private final Map<Integer, Endpoint> endpoints = new HashMap<>();

    public Container() {
        this(null);
    }

    public Container(EventLoopGroup defaultEventLoopGroup) {
        this.defaultEventLoopGroup = defaultEventLoopGroup;
    }

    public void registerEndpoint(final int port, ServerBootstrap boostrap) throws InterruptedException {
        synchronized (endpoints) {
            Endpoint endpoint = endpoints.get(port);
            if (endpoint != null)
                endpoint.reconfigure(boostrap);
            else {
                endpoint = new Endpoint(port, boostrap);
                endpoints.put(port, endpoint);
                initializeEventLoop(boostrap);
                endpoint.start();
                endpoint.closeFuture().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        synchronized (endpoints) {
                            endpoints.remove(port);
                            endpoints.notifyAll();
                        }
                    }
                });
            }
        }
    }

    public void run() throws InterruptedException {
        synchronized (endpoints) {
            while (endpoints.size() > 0)
                endpoints.wait();
        }
    }

    private void initializeEventLoop(ServerBootstrap boostrap) {
        if (boostrap.group() == null) {
            if (defaultEventLoopGroup == null)
                throw new IllegalStateException("Bootstrap provides no event loop group and container has no default event loop group set");
            boostrap.group(defaultEventLoopGroup);
        }
    }
}
