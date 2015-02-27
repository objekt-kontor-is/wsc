package de.objektkontor.wsc.container.core.operation;

import de.objektkontor.wsc.container.Resource;
import de.objektkontor.wsc.container.core.Repository;

public class RegisterResource extends AbstractResourceOperation {

    public RegisterResource(Resource resource) {
        super(resource);
    }

    @Override
    protected void execute(Repository repository) throws Exception {
        repository.register(resource);
    }

    @Override
    protected void revert(Repository repository) throws Exception {
        repository.ungister(resource);
    }
}
