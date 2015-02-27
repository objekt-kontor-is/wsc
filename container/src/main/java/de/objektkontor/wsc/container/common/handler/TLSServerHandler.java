package de.objektkontor.wsc.container.common.handler;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLEngine;

import de.objektkontor.wsc.container.common.config.TLSServerConfig;

public class TLSServerHandler extends TLSHandler {

    protected final TLSServerConfig config;

    public TLSServerHandler(TLSServerConfig config) throws GeneralSecurityException, IOException {
        super(config);
        this.config = config;
    }

    @Override
    public String name() {
        return "TLS (Server)";
    }

    @Override
    protected SSLEngine createSSLEngine() {
        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(false);
        sslEngine.setNeedClientAuth(config.isNeedsClientAuthentication());
        return sslEngine;
    }
}