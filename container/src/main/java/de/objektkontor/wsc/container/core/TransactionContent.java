package de.objektkontor.wsc.container.core;

import de.objektkontor.wsc.container.Resource;
import de.objektkontor.wsc.container.ResourceId;


public interface TransactionContent {

    public abstract void register(Resource resource);

    public abstract void ungister(Resource resource);

    public abstract void connect(ResourceId<?> sourceId, ResourceId<?> targetId);

    public abstract void disconnect(ResourceId<?> sourceId, ResourceId<?> targetId);
}
