package de.objektkontor.wsc.container.common.config;

import de.objektkontor.config.annotation.ConfigParameter;


public class TLSServerConfig extends TLSConfig {

    @ConfigParameter
    private boolean needsClientAuthentication;

    public boolean isNeedsClientAuthentication() {
        return needsClientAuthentication;
    }

    public TLSServerConfig setNeedsClientAuthentication(boolean needsClientAuthentication) {
        this.needsClientAuthentication = needsClientAuthentication;
        return this;
    }

    @Override
    public TLSServerConfig setKeystoreLocation(String keystoreLocation) {
        super.setKeystoreLocation(keystoreLocation);
        return this;
    }

    @Override
    public TLSServerConfig setKeystorePassword(String keystorePassword) {
        super.setKeystorePassword(keystorePassword);
        return this;
    }

    @Override
    public TLSServerConfig setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        return this;
    }

    @Override
    public TLSServerConfig setTruststoreLocation(String truststoreLocation) {
        super.setTruststoreLocation(truststoreLocation);
        return this;
    }

    @Override
    public TLSServerConfig setTruststorePassword(String truststorePassword) {
        super.setTruststorePassword(truststorePassword);
        return this;
    }
}
