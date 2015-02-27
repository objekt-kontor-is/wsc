package de.objektkontor.wsc.container.core.operation;

import de.objektkontor.wsc.container.Resource;
import de.objektkontor.wsc.container.core.Repository;

public class UnregisterResource extends AbstractResourceOperation {

    public UnregisterResource(Resource resource) {
        super(resource);
    }

    @Override
    protected void execute(Repository repository) throws Exception {
        repository.ungister(resource);
    }

    @Override
    protected void revert(Repository repository) throws Exception {
        repository.register(resource);
    }
}
