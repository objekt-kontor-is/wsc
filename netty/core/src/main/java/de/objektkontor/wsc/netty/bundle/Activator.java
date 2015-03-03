package de.objektkontor.wsc.netty.bundle;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(Activator.class);

    @Override
    public void start(BundleContext context) throws Exception {
        Bundle bundle = context.getBundle();
        log.debug("Starting bundle: " + bundle.getSymbolicName() + " (" + bundle.getVersion() + " )");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        Bundle bundle = context.getBundle();
        log.debug("Stopping bundle: " + bundle.getSymbolicName() + " (" + bundle.getVersion() + " )");
    }
}
