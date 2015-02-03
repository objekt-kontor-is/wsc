package de.objektkontor.wsc.server;

import de.objektkontor.config.ConfigLoader;
import de.objektkontor.config.ConfigParameter;
import de.objektkontor.wsc.server.bundle.BundleConfig;

public class ServerConfig {

    @ConfigParameter
    private String systemPackages = "stage,sun.misc,sun.reflect";

    @ConfigParameter
    private String bundleCacheDir = "bundle-cache";

    @ConfigParameter
    private String bundles;

    private BundleConfig[] bundleConfigs;

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

    public String getBundles() {
        return bundles;
    }

    public void setBundles(String bundles) {
        this.bundles = bundles;
    }

    public BundleConfig[] getBundleConfigs() {
        return bundleConfigs;
    }

    public void setBundleConfigs(ConfigLoader configLoader) {
        String bundles = getBundles();
        if (bundles == null || bundles.length() == 0)
            bundleConfigs = new BundleConfig[0];
        else {
            String[] bundleNames = getBundles().split("\\s*,\\s*");
            bundleConfigs = new BundleConfig[bundleNames.length];
            for (int i = 0; i < bundleNames.length; i++)
                bundleConfigs[i] = configLoader.loadConfig("bundle." + bundleNames[i], new BundleConfig());
        }
    }
}
