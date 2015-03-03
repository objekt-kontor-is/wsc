package de.objektkontor.wsc.container.core;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.objektkontor.wsc.netty.JavassistQuirks;

public class AbstractActivator implements BundleActivator {

    private final static Logger log = LoggerFactory.getLogger(AbstractActivator.class);

    private final JavassistQuirks javassistQuirks = new JavassistQuirks();

    @Override
    public void start(BundleContext context) throws Exception {
        Bundle bundle = context.getBundle();
        log.info("Starting bundle: " + bundle.getSymbolicName() + " (" + bundle.getVersion() + " )");
        javassistQuirks.init(this);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        Bundle bundle = context.getBundle();
        log.info("Stopping bundle: " + bundle.getSymbolicName() + " (" + bundle.getVersion() + " )");
        javassistQuirks.close();
    }
}
