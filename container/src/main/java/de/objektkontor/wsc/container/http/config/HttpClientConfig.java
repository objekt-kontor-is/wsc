package de.objektkontor.wsc.container.http.config;

import de.objektkontor.config.ConfigParameter;
import de.objektkontor.wsc.container.common.config.ClientConfig;
import de.objektkontor.wsc.container.common.config.TLSConfig;

public class HttpClientConfig extends ClientConfig {

    @ConfigParameter("tls")
    private TLSConfig tlsConfig = new TLSConfig();

    public TLSConfig getTlsConfig() {
        return tlsConfig;
    }

    public HttpClientConfig setTlsConfig(TLSConfig sslConfig) {
        tlsConfig = sslConfig;
        return this;
    }
}
