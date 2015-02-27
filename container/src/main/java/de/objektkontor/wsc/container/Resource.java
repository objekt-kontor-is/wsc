package de.objektkontor.wsc.container;

public interface Resource {

    public interface Connector<R extends Resource> {

        public final Connector<?> [] NO_CONNECTORS = new Connector<?>[0];

        public abstract Class<R> type();

        public abstract void connect(R resource) throws Exception;

        public abstract void disconnect(R resource) throws Exception;
    }

    public abstract ResourceId<?> id();

    public abstract Connector<?>[] init() throws Exception;

    public abstract boolean ready();

    public abstract void connectionStatus(ResourceId<?> resourceId, boolean active);

    public abstract void start() throws Exception;

    public abstract void stop() throws Exception;

    public abstract void destroy();

}
