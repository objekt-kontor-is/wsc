package de.objektkontor.wsc.container.core.operation;

import de.objektkontor.wsc.container.ResourceId;
import de.objektkontor.wsc.container.core.Repository;

public class DisconnectionRequest extends AbstractConnectionOperation {

    public DisconnectionRequest(ResourceId<?> sourceId, ResourceId<?> targetId) {
        super(sourceId, targetId);
    }

    @Override
    protected void execute(Repository repository) throws Exception {
        repository.disconnect(sourceId, targetId);
    }

    @Override
    protected void revert(Repository repository) throws Exception {
        repository.connect(sourceId, targetId);
    }
}
