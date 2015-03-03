package de.objektkontor.wsc.server;

import de.objektkontor.config.annotation.ConfigParameter;
import de.objektkontor.wsc.server.bundle.BundleConfig;

public class ServerConfig {

    @ConfigParameter
    private String systemPackages = "stage,sun.misc,sun.reflect";

    @ConfigParameter
    private String bundleCacheDir = "bundle-cache";

    @ConfigParameter
    private BundleConfig[] bundles;

    public String getSystemPackages() {
        return systemPackages;
    }

    public void setSystemPackages(String systemPackages) {
        this.systemPackages = systemPackages;
    }

    public String getBundleCacheDir() {
        return bundleCacheDir;
    }

    public void setBundleCacheDir(String bundleCacheDir) {
        this.bundleCacheDir = bundleCacheDir;
    }

    public BundleConfig[] getBundles() {
        return bundles;
    }

    public void setBundles(BundleConfig[] bundles) {
        this.bundles = bundles;
    }
}
