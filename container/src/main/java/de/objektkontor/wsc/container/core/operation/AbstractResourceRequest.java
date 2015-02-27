package de.objektkontor.wsc.container.core.operation;

import de.objektkontor.wsc.container.ResourceId;
import de.objektkontor.wsc.container.core.RepositoryOperation;

public abstract class AbstractResourceRequest extends RepositoryOperation {

    protected final ResourceId<?> resourceId;

    public AbstractResourceRequest(ResourceId<?> resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[resourceId = " + resourceId + "]";
    }
}
