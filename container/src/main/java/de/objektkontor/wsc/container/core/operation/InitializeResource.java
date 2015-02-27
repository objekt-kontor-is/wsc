package de.objektkontor.wsc.container.core.operation;

import de.objektkontor.wsc.container.Resource;
import de.objektkontor.wsc.container.core.Repository;

public class InitializeResource extends AbstractResourceOperation {

    public InitializeResource(Resource resource) {
        super(resource);
    }

    @Override
    protected void execute(Repository repository) throws Exception {
        repository.init(resource.id());
    }

    @Override
    protected void revert(Repository repository) throws Exception {
        resource.destroy();
    }
}
