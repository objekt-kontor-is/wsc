package de.objektkontor.wsc.container.core.operation;

import de.objektkontor.wsc.container.Resource;
import de.objektkontor.wsc.container.core.Repository;

public class ValidateResource extends AbstractResourceOperation {

    public ValidateResource(Resource resource) {
        super(resource);
    }

    @Override
    protected void execute(Repository repository) throws Exception {
        repository.validate(resource.id());
    }

    @Override
    protected void revert(Repository repository) throws Exception {
    }
}
