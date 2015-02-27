package de.objektkontor.wsc.bundle;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.objektkontor.wsc.container.Dispatcher;
import de.objektkontor.wsc.container.Processor;
import de.objektkontor.wsc.container.ResourceId;
import de.objektkontor.wsc.container.core.Container;
import de.objektkontor.wsc.container.core.Transaction;
import de.objektkontor.wsc.container.core.TransactionContent;

public class ProcessorTracker implements ServiceTrackerCustomizer<Processor<?>, Processor<?>> {

    private final Container container;
    private final BundleContext context;

    public ProcessorTracker(Container container, BundleContext context) {
        this.container = container;
        this.context = context;
    }

    @Override
    public Processor<?> addingService(ServiceReference<Processor<?>> reference) {
        final Processor<?> processor = context.getService(reference);
        final ResourceId<?> [] dispatcherIds = getDispatcherIds(reference);
        boolean success = container.execute(new Transaction() {
            @Override
            protected void prepare(TransactionContent content) {
                content.register(processor);
                for (ResourceId<?> dispatcherId : dispatcherIds)
                    content.connect(processor.id(), dispatcherId);
            }
        });
        return success ? processor : null;
    }

    @Override
    public void modifiedService(ServiceReference<Processor<?>> reference, Processor<?> processor) {
    }

    @Override
    public void removedService(ServiceReference<Processor<?>> reference, Processor<?> processor) {
    }

    private ResourceId<?> [] getDispatcherIds(ServiceReference<Processor<?>> reference) {
        Object value = reference.getProperty(Dispatcher.DISPATCHER_ID_PROPERTY);
        return value.getClass().isArray() ? (ResourceId<?>[]) value : new ResourceId<?>[] { (ResourceId<?>) value };
    }
}