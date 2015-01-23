package org.okis.wsc.api;

import io.netty.bootstrap.Bootstrap;

public interface Endpoint {

    int getPort();

    Bootstrap getBootstrap();
}
