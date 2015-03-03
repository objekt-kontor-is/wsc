package de.objektkontor.wsc.container.common.config;

import de.objektkontor.config.annotation.ConfigParameter;

public class ServerConfig {

    @ConfigParameter
    private int port;

    @ConfigParameter
    private int socketBacklog = 64;

    @ConfigParameter
    private boolean clientTcpNoDelay = true;

    @ConfigParameter
    private boolean clientKeepAlive = true;

    @ConfigParameter
    private int clientReadTimeout = 0;

    @ConfigParameter("tls")
    private TLSServerConfig tlsConfig = new TLSServerConfig();

    public int getPort() {
        return port;
    }

    public ServerConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public int getSocketBacklog() {
        return socketBacklog;
    }

    public ServerConfig setSocketBacklog(int socketBacklog) {
        this.socketBacklog = socketBacklog;
        return this;
    }

    public boolean isClientTcpNoDelay() {
        return clientTcpNoDelay;
    }

    public ServerConfig setClientTcpNoDelay(boolean clientTcpNoDelay) {
        this.clientTcpNoDelay = clientTcpNoDelay;
        return this;
    }

    public boolean isClientKeepAlive() {
        return clientKeepAlive;
    }

    public ServerConfig setClientKeepAlive(boolean clientKeepAlive) {
        this.clientKeepAlive = clientKeepAlive;
        return this;
    }

    public int getClientReadTimeout() {
        return clientReadTimeout;
    }

    public ServerConfig setClientReadTimeout(int clientReadTimeout) {
        this.clientReadTimeout = clientReadTimeout;
        return this;
    }

    public TLSServerConfig getTlsConfig() {
        return tlsConfig;
    }

    public ServerConfig setTlsConfig(TLSServerConfig tlsConfig) {
        this.tlsConfig = tlsConfig;
        return this;
    }
}
