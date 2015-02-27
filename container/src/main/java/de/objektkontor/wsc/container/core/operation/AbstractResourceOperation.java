package de.objektkontor.wsc.container.core.operation;

import de.objektkontor.wsc.container.Resource;
import de.objektkontor.wsc.container.core.RepositoryOperation;

public abstract class AbstractResourceOperation extends RepositoryOperation {

    protected final Resource resource;

    public AbstractResourceOperation(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[resource = " + resource.id() + "]";
    }
}
