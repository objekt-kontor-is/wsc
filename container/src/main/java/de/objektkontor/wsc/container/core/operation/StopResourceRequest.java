package de.objektkontor.wsc.container.core.operation;

import de.objektkontor.wsc.container.Resource;
import de.objektkontor.wsc.container.ResourceId;
import de.objektkontor.wsc.container.core.Repository;

public class StopResourceRequest extends AbstractResourceRequest {

    private Resource resource;

    public StopResourceRequest(ResourceId<?> resourceId) {
        super(resourceId);
    }

    @Override
    protected void execute(Repository repository) throws Exception {
        resource = repository.getResource(resourceId);
        if (resource != null && repository.isResourceActive(resource.id()) && ! resource.ready()) {
            resource.stop();
            repository.resourceActive(resource.id(), false);
        }
    }

    @Override
    protected void revert(Repository repository) throws Exception {
        if (resource != null) {
            resource.start();
            repository.resourceActive(resource.id(), true);
        }
    }
}
