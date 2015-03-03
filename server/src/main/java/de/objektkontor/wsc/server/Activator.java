package de.objektkontor.wsc.server;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.objektkontor.wsc.server.bundle.BundleConfig;
import de.objektkontor.wsc.server.bundle.BundleKey;
import de.objektkontor.wsc.server.bundle.BundleLocator;

public class Activator implements BundleActivator {

    private final static Logger log = LoggerFactory.getLogger(Activator.class);

    private final static Name BUNDLE_SYMBOLIC_NAME = new Name("Bundle-SymbolicName");
    private final static Name BUNDLE_VERSION = new Name("Bundle-Version");

    private final Map<BundleKey, Bundle> bundles = new HashMap<>();
    private final Map<String, BundleKey> locations = new HashMap<>();

    private final ServerConfig config;

    private BundleContext context;

    public Activator(ServerConfig config) {
        this.config = config;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        this.context = context;
        initBundles();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        this.context = null;
        bundles.clear();
        locations.clear();
    }

    public BundleContext context() {
        return context;
    }

    private List<BundleLocator> getBundles(File bundleDir) throws IOException {
        if (!bundleDir.exists())
            throw new IOException("Bundle dir not found: " + bundleDir);
        File[] bundleFiles = bundleDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && file.getName().endsWith(".jar");
            }
        });
        List<BundleLocator> result = new ArrayList<>(bundleFiles.length);
        for (File bundleFile : bundleFiles)
            try (JarFile jarFile = new JarFile(bundleFile)) {
                Manifest manifest = jarFile.getManifest();
                String name = manifest.getMainAttributes().getValue(BUNDLE_SYMBOLIC_NAME);
                String version = manifest.getMainAttributes().getValue(BUNDLE_VERSION);
                if (name != null && version != null) {
                    BundleKey key = new BundleKey(name, new Version(version));
                    BundleLocator locator = new BundleLocator(key, "file:" + bundleFile.getAbsolutePath());
                    result.add(locator);
                }
            }
        return result;
    }

    private BundleLocator getBundle(BundleConfig bundleConfig) throws BundleException {
        BundleKey bundleKey = locations.get(bundleConfig.getLocation());
        if (bundleKey == null) {
            Bundle bundle = context.installBundle(bundleConfig.getLocation());
            return new BundleLocator(bundle);
        }
        return new BundleLocator(bundleKey, bundleConfig.getLocation());
    }

    private void initBundles() throws BundleException, IOException {
        for (Bundle bundle : context.getBundles()) {
            BundleKey bundleKey = new BundleKey(bundle);
            bundles.put(bundleKey, bundle);
            locations.put(bundle.getLocation(), bundleKey);
        }
        for (BundleConfig bundleConfig : config.getBundles()) {
            if (bundleConfig.getDependenciesDir() != null) {
                File dependenciesDir = new File(bundleConfig.getDependenciesDir());
                for (BundleLocator bundleLocator : getBundles(dependenciesDir))
                    initBundle(bundleLocator);
            }
            BundleLocator bundleLocator = getBundle(bundleConfig);
            initBundle(bundleLocator);
        }
    }

    private void initBundle(BundleLocator bundleLocator) throws BundleException {
        log.debug("Initializing bundle: " + bundleLocator.getKey());
        Bundle bundle = bundles.get(bundleLocator.getKey());
        if (bundle == null) {
            log.debug("--> installing new bundle");
            bundle = context.installBundle(bundleLocator.getLocation());
            bundles.put(bundleLocator.getKey(), bundle);
            locations.put(bundleLocator.getLocation(), bundleLocator.getKey());
        } else {
            log.debug("--> update existing bundle: ");
            bundle.update();
        }
        bundle.start();
    }
}
