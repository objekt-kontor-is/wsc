package de.objektkontor.wsc.container.core.operation;

import de.objektkontor.wsc.container.Resource;
import de.objektkontor.wsc.container.ResourceId;
import de.objektkontor.wsc.container.core.Repository;

public class StartResourceRequest extends AbstractResourceRequest {

    private Resource resource;

    public StartResourceRequest(ResourceId<?> resourceId) {
        super(resourceId);
    }

    @Override
    protected void execute(Repository repository) throws Exception {
        resource = repository.getResource(resourceId);
        if (resource != null && ! repository.isResourceActive(resourceId) && resource.ready()) {
            resource.start();
            repository.resourceActive(resource.id(), true);
        }
    }

    @Override
    protected void revert(Repository repository) throws Exception {
        if (resource != null) {
            repository.resourceActive(resourceId, false);
            resource.stop();
        }
    }
}
