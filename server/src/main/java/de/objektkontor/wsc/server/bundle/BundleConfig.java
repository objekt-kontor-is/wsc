package de.objektkontor.wsc.server.bundle;

import de.objektkontor.config.annotation.ConfigParameter;

public class BundleConfig {

    @ConfigParameter
    private String location;

    @ConfigParameter
    private String dependenciesDir;

    public String getLocation() {
        return location;
    }

    public void setLocation(String url) {
        location = url;
    }

    public String getDependenciesDir() {
        return dependenciesDir;
    }

    public void setDependenciesDir(String dependenciesDir) {
        this.dependenciesDir = dependenciesDir;
    }
}
