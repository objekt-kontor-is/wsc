package de.objektkontor.wsc.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.objektkontor.config.ConfigLoader;
import de.objektkontor.config.PropertyBackend;

public class Server {

    private final static Logger log = LoggerFactory.getLogger(Server.class);

    private final static Name EXPORT_PACKAGE = new Name("Export-Package");

    private static Map<Object, Object> createFelixConfig(Activator activator, ServerConfig config) throws IOException {
        Map<Object, Object> result = new HashMap<>();
        List<BundleActivator> activators = new LinkedList<>();
        activators.add(activator);
        result.put(Constants.FRAMEWORK_STORAGE, config.getBundleCacheDir());
        result.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, getSystemPackages(config));
        result.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, activators);
        result.put(FelixConstants.LOG_LOGGER_PROP, new de.objektkontor.wsc.server.Logger(log));
        result.put(FelixConstants.LOG_LEVEL_PROP, "4");
        return result;
    }

    private static String getSystemPackages(ServerConfig config) throws IOException {
        String packages = config.getSystemPackages();
        try (InputStream in = Server.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF")) {
            Manifest manifest = new Manifest(in);
            String exports = manifest.getMainAttributes().getValue(EXPORT_PACKAGE);
            packages = packages == null || packages.length() == 0 ? exports : exports + "," + packages;
        }
        return packages;
    }

    public static void main(String... args) throws Exception {
        System.getProperties().setProperty("java.protocol.handler.pkgs", "org.ops4j.pax.url");

        ConfigLoader configLoader = new ConfigLoader(new PropertyBackend(), "wsc", true);
        ServerConfig config = configLoader.loadConfig(new ServerConfig());

        Activator activator = new Activator(config);
        final Felix felix = new Felix(createFelixConfig(activator, config));

        Runtime.getRuntime().addShutdownHook(new Thread("Felix Shutdown Hook") {
            @Override
            public void run() {
                try {
                    log.info("Stopping embedded osgi container");
                    felix.stop();
                    felix.waitForStop(0);
                } catch (Exception e) {
                    log.error("Error stopping osgi container: " + e);
                }
            }
        });

        log.info("Initializing embedded osgi container");
        felix.init();

        log.info("Starting embedded osgi container");
        felix.start();
        felix.waitForStop(0);
    }
}
