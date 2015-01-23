package org.okis.wsc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.HashMap;
import java.util.Map;

public class Container {

    private final Map<Integer, Endpoint> endpoints = new HashMap<>();

    public void registerEndpoint(final int port, ServerBootstrap boostrap) throws InterruptedException {
        synchronized (endpoints) {
            Endpoint endpoint = endpoints.get(port);
            if (endpoint != null)
                endpoint.reconfigure(boostrap);
            else {
                endpoint = new Endpoint(port, boostrap);
                endpoints.put(port, endpoint);
                endpoint.start();
                endpoint.closeFuture().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        synchronized (endpoints) {
                            endpoints.remove(port);
                        }
                    }
                });
            }
        }
    }
}
