package de.objektkontor.wsc.container.http.config;

import de.objektkontor.config.ConfigParameter;
import de.objektkontor.wsc.container.common.config.ServerConfig;
import de.objektkontor.wsc.container.common.config.TLSServerConfig;

public class HttpServerConfig extends ServerConfig {

    @ConfigParameter
    private int maxContentLength = 1024 * 1024;

    public int getMaxContentLength() {
        return maxContentLength;
    }

    public HttpServerConfig setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
        return this;
    }

    @Override
    public HttpServerConfig setPort(int port) {
        super.setPort(port);
        return this;
    }

    @Override
    public HttpServerConfig setSocketBacklog(int socketBacklog) {
        super.setSocketBacklog(socketBacklog);
        return this;
    }

    @Override
    public HttpServerConfig setClientTcpNoDelay(boolean clientTcpNoDelay) {
        super.setClientTcpNoDelay(clientTcpNoDelay);
        return this;
    }

    @Override
    public HttpServerConfig setClientKeepAlive(boolean clientKeepAlive) {
        super.setClientKeepAlive(clientKeepAlive);
        return this;
    }

    @Override
    public HttpServerConfig setClientReadTimeout(int clientReadTimeout) {
        super.setClientReadTimeout(clientReadTimeout);
        return this;
    }

    @Override
    public HttpServerConfig setTlsConfig(TLSServerConfig tlsConfig) {
        super.setTlsConfig(tlsConfig);
        return this;
    }
}
