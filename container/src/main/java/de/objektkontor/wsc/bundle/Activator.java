package de.objektkontor.wsc.bundle;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import de.objektkontor.wsc.container.Dispatcher;
import de.objektkontor.wsc.container.Endpoint;
import de.objektkontor.wsc.container.Processor;
import de.objektkontor.wsc.container.core.AbstractActivator;
import de.objektkontor.wsc.container.core.Container;

public class Activator extends AbstractActivator {

    private Container container;

    private ServiceTracker<Endpoint, Endpoint> endpointTracker;
    private ServiceTracker<Processor<?>, Processor<?>> processorTracker;
    private ServiceTracker<Dispatcher<?,?,?>, Dispatcher<?,?,?>> dispatcherTracker;

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        container = new Container();
        endpointTracker = new ServiceTracker<Endpoint, Endpoint>(context, Endpoint.class.getName(), new EndpointTracker(container, context));
        processorTracker = new ServiceTracker<Processor<?>, Processor<?>>(context, Processor.class.getName(), new ProcessorTracker(container, context));
        dispatcherTracker = new ServiceTracker<Dispatcher<?,?,?>, Dispatcher<?,?,?>>(context, Dispatcher.class.getName(), new DispatcherTracker(container, context));
        processorTracker.open();
        dispatcherTracker.open();
        endpointTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        endpointTracker.close();
        processorTracker.close();
        dispatcherTracker.close();
        container.destroy();
        container = null;
    }
}