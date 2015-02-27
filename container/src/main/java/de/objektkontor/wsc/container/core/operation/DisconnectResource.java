package de.objektkontor.wsc.container.core.operation;

import de.objektkontor.wsc.container.Resource;
import de.objektkontor.wsc.container.Resource.Connector;
import de.objektkontor.wsc.container.ResourceId;
import de.objektkontor.wsc.container.core.Repository;

public class DisconnectResource extends AbstractConnectionOperation {

    public DisconnectResource(ResourceId<?> source, ResourceId<?> target) {
        super(source, target);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void execute(Repository repository) throws Exception {
        Connector connector = repository.getConnector(sourceId.type(), targetId);
        if (connector == null)
            throw new UnsupportedOperationException("Connection operation is not supported by target resource");
        Resource source = repository.getResource(sourceId);
        Resource target = repository.getResource(targetId);
        if (source != null && target != null) {
            connector.disconnect(source);
            source.connectionStatus(targetId, false);
        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void revert(Repository repository) throws Exception {
        Connector connector = repository.getConnector(sourceId.type(), targetId);
        Resource source = repository.getResource(sourceId);
        Resource target = repository.getResource(targetId);
        if (source != null && target != null) {
            connector.connect(source);
            source.connectionStatus(targetId, true);
        }
    }
}
