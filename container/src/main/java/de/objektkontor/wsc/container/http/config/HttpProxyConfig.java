package de.objektkontor.wsc.container.http.config;

import de.objektkontor.config.ObservableConfig;
import de.objektkontor.config.annotation.ConfigParameter;

public class HttpProxyConfig extends ObservableConfig {

    @ConfigParameter("")
    private HttpServerConfig serverConfig = new HttpServerConfig();

    @ConfigParameter("client")
    private HttpClientConfig clientConfig = new HttpClientConfig();

    @ConfigParameter
    private int maxContentLength = 1024 * 1024;

    public HttpServerConfig getServerConfig() {
        return serverConfig;
    }

    public HttpProxyConfig setServerConfig(HttpServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        return this;
    }

    public HttpClientConfig getClientConfig() {
        return clientConfig;
    }

    public HttpProxyConfig setClientConfig(HttpClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        return this;
    }

    public int getMaxContentLength() {
        return maxContentLength;
    }

    public HttpProxyConfig setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
        return this;
    }
}
