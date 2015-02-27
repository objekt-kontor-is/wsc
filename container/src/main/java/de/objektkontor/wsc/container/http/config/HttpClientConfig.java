package de.objektkontor.wsc.container.http.config;

import de.objektkontor.config.ConfigParameter;
import de.objektkontor.wsc.container.common.config.ClientConfig;

public class HttpClientConfig extends ClientConfig {

    @ConfigParameter
    private int maxContentLength = 1024 * 1024;

    public int getMaxContentLength() {
        return maxContentLength;
    }

    public HttpClientConfig setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
        return this;
    }

    @Override
    public HttpClientConfig setHost(String host) {
        super.setHost(host);
        return this;
    }

    @Override
    public HttpClientConfig setPort(int port) {
        super.setPort(port);
        return this;
    }

    @Override
    public HttpClientConfig setTcpNoDelay(boolean tcpNoDelay) {
        super.setTcpNoDelay(tcpNoDelay);
        return this;
    }

    @Override
    public HttpClientConfig setKeepAlive(boolean keepAlive) {
        super.setKeepAlive(keepAlive);
        return this;
    }

    @Override
    public HttpClientConfig setConnectTimeout(int connectTimeout) {
        super.setConnectTimeout(connectTimeout);
        return this;
    }

    @Override
    public HttpClientConfig setReadTimeout(int readTimeout) {
        super.setReadTimeout(readTimeout);
        return this;
    }
}
