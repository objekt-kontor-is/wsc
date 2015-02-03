package de.objektkontor.wsc.server.bundle;

import org.osgi.framework.Bundle;

public class BundleLocator {

    private final BundleKey key;
    private final String location;

    public BundleLocator(BundleKey key, String location) {
        this.key = key;
        this.location = location;
    }

    public BundleLocator(Bundle bundle) {
        key = new BundleKey(bundle);
        location = bundle.getLocation();
    }

    public BundleKey getKey() {
        return key;
    }

    public String getLocation() {
        return location;
    }
}
