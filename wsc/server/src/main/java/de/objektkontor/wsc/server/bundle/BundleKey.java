package de.objektkontor.wsc.server.bundle;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

public class BundleKey {

    private final String symbolicName;
    private final Version version;

    public BundleKey(Bundle bundle) {
        symbolicName = bundle.getSymbolicName();
        version = bundle.getVersion();
    }

    public BundleKey(String symbolicName, Version version) {
        this.symbolicName = symbolicName;
        this.version = version;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + symbolicName.hashCode();
        result = prime * result + version.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BundleKey other = (BundleKey) obj;
        if (!version.equals(other.version))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return symbolicName + " (" + version + ")";
    }
}
