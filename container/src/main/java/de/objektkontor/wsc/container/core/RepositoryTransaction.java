package de.objektkontor.wsc.container.core;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.objektkontor.wsc.container.Resource;
import de.objektkontor.wsc.container.ResourceId;
import de.objektkontor.wsc.container.core.operation.ConnectResource;
import de.objektkontor.wsc.container.core.operation.ConnectionRequest;
import de.objektkontor.wsc.container.core.operation.DisconnectResource;
import de.objektkontor.wsc.container.core.operation.DisconnectionRequest;
import de.objektkontor.wsc.container.core.operation.InitializeResource;
import de.objektkontor.wsc.container.core.operation.RegisterResource;
import de.objektkontor.wsc.container.core.operation.StartResourceRequest;
import de.objektkontor.wsc.container.core.operation.StopResourceRequest;
import de.objektkontor.wsc.container.core.operation.UnregisterResource;
import de.objektkontor.wsc.container.core.operation.ValidateResource;

public class RepositoryTransaction implements TransactionContent {

    private final static Logger log = LoggerFactory.getLogger(RepositoryTransaction.class);

    private final Repository repository;

    private final List<RepositoryOperation> operations = new ArrayList<>();

    public RepositoryTransaction(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void register(Resource resource) {
        operations.add(new RegisterResource(resource));
        operations.add(new InitializeResource(resource));
        operations.add(new ValidateResource(resource));
        for (ResourceId<?> targetId : repository.getConnectionTargets(resource.id())) {
            operations.add(new ConnectResource(resource.id(), targetId));
            operations.add(new StartResourceRequest(targetId));
        }
        for (ResourceId<?> sourceId : repository.getConnectionSources(resource.id())) {
            operations.add(new ConnectResource(sourceId, resource.id()));
            operations.add(new StartResourceRequest(sourceId));
        }
        operations.add(new StartResourceRequest(resource.id()));
    }

    @Override
    public void ungister(Resource resource) {
        for (ResourceId<?> sourceId : repository.getConnectionSources(resource.id())) {
            operations.add(new DisconnectResource(sourceId, resource.id()));
            operations.add(new StopResourceRequest(sourceId));
        }
        for (ResourceId<?> targetId : repository.getConnectionTargets(resource.id())) {
            operations.add(new DisconnectResource(resource.id(), targetId));
            operations.add(new StopResourceRequest(targetId));
        }
        operations.add(new StopResourceRequest(resource.id()));
        operations.add(new UnregisterResource(resource));
    }

    @Override
    public void connect(ResourceId<?> sourceId, ResourceId<?> targetId) {
        operations.add(new ConnectionRequest(sourceId, targetId));
        operations.add(new ConnectResource(sourceId, targetId));
        operations.add(new StartResourceRequest(sourceId));
        operations.add(new StartResourceRequest(targetId));
    }

    @Override
    public void disconnect(ResourceId<?> sourceId, ResourceId<?> targetId) {
        operations.add(new DisconnectResource(sourceId, targetId));
        operations.add(new DisconnectionRequest(sourceId, targetId));
        operations.add(new StopResourceRequest(sourceId));
        operations.add(new StopResourceRequest(targetId));
    }

    public boolean commit() {
        if (log.isDebugEnabled())
            log.debug("Commiting repository transaction. Operation to process: " + operations.size());
        for (int execIndex = 0; execIndex < operations.size(); execIndex ++) {
            RepositoryOperation operation = operations.get(execIndex);
            try {
                if (log.isDebugEnabled())
                    log.debug("Executing operation: " + operation);
                operation.execute(repository);
            } catch (Exception execError) {
                log.error("Unable to commit repository transaction. Error executing operation: " + operation, execError);
                for (int revertIndex = execIndex - 1; revertIndex >= 0; revertIndex --) {
                    operation = operations.get(revertIndex);
                    try {
                        if (log.isDebugEnabled())
                            log.debug("Reverting operation: " + operation);
                        operation.revert(repository);
                    } catch (Exception revertError) {
                        log.error("Unable to revert previously executed operation: " + operation, revertError);
                    }
                }
                return false;
            }
        }
        return true;
    }
}
