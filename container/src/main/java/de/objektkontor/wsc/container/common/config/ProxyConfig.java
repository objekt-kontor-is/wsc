package de.objektkontor.wsc.container.common.config;

import de.objektkontor.config.ObservableConfig;
import de.objektkontor.config.annotation.ConfigParameter;

public class ProxyConfig extends ObservableConfig {

    @ConfigParameter("")
    private ServerConfig serverConfig = new ServerConfig();

    @ConfigParameter("client")
    private ClientConfig clientConfig = new ClientConfig();

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public ProxyConfig setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        return this;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public ProxyConfig setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        return this;
    }
}
