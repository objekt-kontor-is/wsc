package de.objektkontor.wsc.bundle;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.objektkontor.wsc.container.Dispatcher;
import de.objektkontor.wsc.container.Endpoint;
import de.objektkontor.wsc.container.ResourceId;
import de.objektkontor.wsc.container.core.Container;
import de.objektkontor.wsc.container.core.Transaction;
import de.objektkontor.wsc.container.core.TransactionContent;

public class EndpointTracker implements ServiceTrackerCustomizer<Endpoint, Endpoint> {

    private final Container container;
    private final BundleContext context;

    public EndpointTracker(Container container, BundleContext context) {
        this.container = container;
        this.context = context;
    }

    @Override
    public Endpoint addingService(ServiceReference<Endpoint> reference) {
        final Endpoint endpoint = context.getService(reference);
        final ResourceId<?> dispatcherId = getDispatcherId(reference);
        boolean success = container.execute(new Transaction() {
            @Override
            protected void prepare(TransactionContent content) {
                content.register(endpoint);
                content.connect(endpoint.id(), dispatcherId);
            }
        });
        return success ? endpoint : null;
    }

    @Override
    public void modifiedService(ServiceReference<Endpoint> reference, Endpoint service) {
    }

    @Override
    public void removedService(ServiceReference<Endpoint> reference, Endpoint service) {
    }

    private ResourceId<?> getDispatcherId(ServiceReference<Endpoint> reference) {
        return (ResourceId<?>) reference.getProperty(Dispatcher.DISPATCHER_ID_PROPERTY);
    }
}