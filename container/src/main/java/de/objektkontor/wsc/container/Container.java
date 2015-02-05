package de.objektkontor.wsc.container;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.HashMap;
import java.util.Map;

public class Container {

    private final Map<Integer, Endpoint> endpoints = new HashMap<>();

    public void registerEndpoint(Endpoint endpoint) throws InterruptedException {
        final int port = endpoint.getPort();
        synchronized (endpoints) {
            if (endpoints.containsKey(port))
                throw new IllegalStateException("Another endpoint is already registered for port: " + port);
            endpoints.put(port, endpoint);
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

    public void run() throws InterruptedException {
        synchronized (endpoints) {
            while (endpoints.size() > 0)
                endpoints.wait();
        }
    }
}
