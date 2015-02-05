package de.objektkontor.wsc.bundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.objektkontor.wsc.container.Container;
import de.objektkontor.wsc.container.Endpoint;

public class Activator implements BundleActivator {

    private Container container;
    private ServiceTracker<Endpoint, Endpoint> serviceTracker;

    @Override
    public void start(final BundleContext context) throws Exception {
        container = new Container();
        serviceTracker = new ServiceTracker<>(context, Endpoint.class, new ServiceTrackerCustomizer<Endpoint, Endpoint>() {

            @Override
            public Endpoint addingService(ServiceReference<Endpoint> reference) {
                Endpoint endpoint = context.getService(reference);
                try {
                    container.registerEndpoint(endpoint);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return endpoint;
            }

            @Override
            public void modifiedService(ServiceReference<Endpoint> reference, Endpoint service) {
                // TODO Auto-generated method stub
            }

            @Override
            public void removedService(ServiceReference<Endpoint> reference, Endpoint service) {
                // TODO Auto-generated method stub
            }
        });
        serviceTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        serviceTracker.close();
        container = null;
    }
}