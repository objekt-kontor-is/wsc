package de.objektkontor.wsc.container.core.operation;

import de.objektkontor.wsc.container.ResourceId;
import de.objektkontor.wsc.container.core.RepositoryOperation;

public abstract class AbstractConnectionOperation extends RepositoryOperation {

    protected final ResourceId<?> sourceId;

    protected final ResourceId<?> targetId;

    public AbstractConnectionOperation(ResourceId<?> sourceId, ResourceId<?> targetId) {
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[sourceId = " + sourceId + ", targetId = " + targetId + "]";
    }
}
