package org.okis.wsc.config.common;

import org.okis.wsc.config.ConfigParameter;

public class ClientConfig {

    @ConfigParameter
    private String host;

    @ConfigParameter
    private int port;

    @ConfigParameter
    private boolean tcpNoDelay = true;

    @ConfigParameter
    private boolean keepAlive = true;

    @ConfigParameter
    private int connectTimeout = 0;

    @ConfigParameter
    private int readTimeout = 0;

    public String getHost() {
        return host;
    }

    public ClientConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ClientConfig  setPort(int port) {
        this.port = port;
        return this;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public ClientConfig setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
        return this;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public ClientConfig setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public ClientConfig setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public ClientConfig setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }
}
