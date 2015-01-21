package org.okis.wsc;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private final static Logger log = LoggerFactory.getLogger(Activator.class);

    private Thread serverThread = null;
    
    @Override
    public void start(BundleContext context) throws Exception {
        log.info("Starting bundle.");
        
        serverThread = new Thread(Initializer.createServer());
        serverThread.start();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        log.info("Stopping bundle.");

        if (serverThread != null) {
            log.info("Shutting down server-thread.");
            serverThread.interrupt();
        }
    }
}