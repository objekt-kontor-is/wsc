package de.objektkontor.wsc.bundle;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.objektkontor.wsc.container.Dispatcher;
import de.objektkontor.wsc.container.core.Container;
import de.objektkontor.wsc.container.core.Transaction;
import de.objektkontor.wsc.container.core.TransactionContent;

public class DispatcherTracker implements ServiceTrackerCustomizer<Dispatcher<?,?,?>, Dispatcher<?,?,?>> {

    private final Container container;
    private final BundleContext context;

    public DispatcherTracker(Container container, BundleContext context) {
        this.container = container;
        this.context = context;
    }

    @Override
    public Dispatcher<?,?,?> addingService(ServiceReference<Dispatcher<?,?,?>> reference) {
        final Dispatcher<?,?,?> dispatcher = context.getService(reference);
        boolean success = container.execute(new Transaction() {
            @Override
            protected void prepare(TransactionContent content) {
                content.register(dispatcher);
            }
        });
        return success ? dispatcher : null;
    }

    @Override
    public void modifiedService(ServiceReference<Dispatcher<?,?,?>> reference, Dispatcher<?,?,?> service) {
    }

    @Override
    public void removedService(ServiceReference<Dispatcher<?,?,?>> reference, Dispatcher<?,?,?> service) {
    }
}