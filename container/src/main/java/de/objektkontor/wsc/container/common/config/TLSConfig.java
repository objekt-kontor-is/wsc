package de.objektkontor.wsc.container.common.config;

import de.objektkontor.config.ConfigParameter;


public class TLSConfig {

    @ConfigParameter
    private boolean enabled = false;

    @ConfigParameter
    private String keystoreLocation;

    @ConfigParameter
    private String keystorePassword;

    @ConfigParameter
    private String truststoreLocation;

    @ConfigParameter
    private String truststorePassword;

    public String getKeystoreLocation() {
        return keystoreLocation;
    }

    public TLSConfig setKeystoreLocation(String keystoreLocation) {
        this.keystoreLocation = keystoreLocation;
        return this;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public TLSConfig setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
        return this;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public TLSConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getTruststoreLocation() {
        return truststoreLocation;
    }

    public TLSConfig setTruststoreLocation(String truststoreLocation) {
        this.truststoreLocation = truststoreLocation;
        return this;
    }

    public String getTruststorePassword() {
        return truststorePassword;
    }

    public TLSConfig setTruststorePassword(String truststorePassword) {
        this.truststorePassword = truststorePassword;
        return this;
    }
}
