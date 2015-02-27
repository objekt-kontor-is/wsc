package de.objektkontor.wsc.container.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.objektkontor.wsc.container.Resource;
import de.objektkontor.wsc.container.ResourceId;

public class AbstractResource implements Resource {

    private final static Logger log = LoggerFactory.getLogger(AbstractResource.class);

    private final ResourceId<?> id;

    private int activeConnections = 0;

    public AbstractResource(Class<?> type, String id) {
        this.id = new ResourceId<>(type, id);
    }

    @Override
    public ResourceId<?> id() {
        return id;
    }

    @Override
    public Connector<?>[] init() throws Exception {
        log.info("Resource initialised: " + id);
        return Connector.NO_CONNECTORS;
    }

    @Override
    public boolean ready() {
        return activeConnections > 0;
    }

    @Override
    public void connectionStatus(ResourceId<?> resourceId, boolean active) {
        activeConnections += active ? 1 : -1;
    }

    @Override
    public void start() throws Exception {
        log.info("Resource started: " + id);
    }

    @Override
    public void stop() throws Exception {
        log.info("Resource stopped: " + id);
    }

    @Override
    public void destroy() {
        log.info("Resource destroyed: " + id);
    }
}
