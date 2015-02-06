package de.objektkontor.wsc.container.http.config;

import de.objektkontor.config.ConfigParameter;
import de.objektkontor.wsc.container.common.config.ServerConfig;
import de.objektkontor.wsc.container.common.config.TLSServerConfig;

public class HttpServerConfig extends ServerConfig {

    @ConfigParameter("tls")
    private TLSServerConfig tlsConfig = new TLSServerConfig();


    public TLSServerConfig getTlsConfig() {
        return tlsConfig;
    }

    public HttpServerConfig setTlsConfig(TLSServerConfig tlsConfig) {
        this.tlsConfig = tlsConfig;
        return this;
    }
}
