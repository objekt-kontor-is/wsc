package de.objektkontor.wsc.container.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.objektkontor.wsc.container.Resource;
import de.objektkontor.wsc.container.Resource.Connector;
import de.objektkontor.wsc.container.ResourceId;

public class Repository implements TransactionContent {

    private final Map<ResourceId<?>, Resource> resources = new HashMap<>();

    private final Map<ResourceId<?>, Set<ResourceId<?>>> connectionTargets = new HashMap<>();

    private final Map<ResourceId<?>, Set<ResourceId<?>>> connectionSources = new HashMap<>();

    private final Map<ResourceId<?>, Map<Class<?>, Connector<?>>> connectors = new HashMap<>();

    private final Map<ResourceId<?>, Boolean> resourceActivations = new HashMap<>();

    private final ValidatorFactory validatorFactory = new ValidatorFactory();

    public boolean empty() {
        return resources.size() == 0;
    }

    public boolean hasResource(ResourceId<?> resourceId) {
        if (resourceId == null)
            throw new IllegalArgumentException("resourceId may not be null");
        assert resourceId != null;
        return resources.containsKey(resourceId);
    }

    @SuppressWarnings("unchecked")
    public <R extends Resource> R getResource(ResourceId<R> resourceId) {
        if (resourceId == null)
            throw new IllegalArgumentException("resourceId may not be null");
        return (R) resources.get(resourceId);
    }

    @Override
    public void register(Resource resource) {
        if (resource == null)
            throw new IllegalArgumentException("resource may not be null");
        if (resources.containsKey(resource.id()))
            throw new IllegalStateException("Resource already registered");
        resources.put(resource.id(), resource);
        connectionTargets.put(resource.id(), new HashSet<ResourceId<?>>());
        connectionSources.put(resource.id(), new HashSet<ResourceId<?>>());
    }

    public void init(ResourceId<?> resourceId) throws Exception {
        if (resourceId == null)
            throw new IllegalArgumentException("resourceId may not be null");
        Resource resource = resources.get(resourceId);
        if (resource == null)
            throw new IllegalStateException("Resource is not registered");
        if (connectors.containsKey(resourceId))
            throw new IllegalStateException("Resource allready initialized");
        Map<Class<?>, Connector<?>> resourceConnectors = new HashMap<>();
        for (Connector<?> connector : resource.init())
            resourceConnectors.put(connector.type(), connector);
        connectors.put(resource.id(), resourceConnectors);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void validate(ResourceId<?> resourceId) throws Exception {
        if (resourceId == null)
            throw new IllegalArgumentException("resourceId may not be null");
        Resource resource = resources.get(resourceId);
        if (resource == null)
            throw new IllegalStateException("Resource is not registered");
        Validator validator = validatorFactory.create(resourceId.type());
        if (validator != null)
            validator.validate(resource);
    }

    @Override
    public void ungister(Resource resource) {
        if (resource == null)
            throw new IllegalArgumentException("resource may not be null");
        if (!resources.containsKey(resource.id()))
            throw new IllegalStateException("Resource is not registered");
        resources.remove(resource.id());
        connectionTargets.remove(resource.id());
        connectionSources.remove(resource.id());
        connectors.remove(resource.id());
    }

    @Override
    public void connect(ResourceId<?> sourceId, ResourceId<?> targetId) {
        if (sourceId == null || targetId == null)
            throw new IllegalArgumentException("sourceId and targetId may not be null");
        Set<ResourceId<?>> targets = connectionTargets.get(sourceId);
        Set<ResourceId<?>> sources = connectionSources.get(targetId);
        if (targets.contains(targetId) || sources.contains(sourceId))
            throw new IllegalStateException("Resources are already connected");
        targets.add(targetId);
        sources.add(sourceId);
    }

    @Override
    public void disconnect(ResourceId<?> sourceId, ResourceId<?> targetId) {
        if (sourceId == null || targetId == null)
            throw new IllegalArgumentException("sourceId and targetId may not be null");
        Set<ResourceId<?>> targets = connectionTargets.get(sourceId);
        Set<ResourceId<?>> sources = connectionSources.get(targetId);
        if (!targets.contains(targetId) || !sources.contains(sourceId))
            throw new IllegalStateException("Resources are not connected");
        targets.remove(targetId);
        sources.remove(sourceId);
    }

    public Set<ResourceId<?>> getConnectionTargets(ResourceId<?> sourceId) {
        Set<ResourceId<?>> targetIds = connectionTargets.get(sourceId);
        if (targetIds == null)
            return Collections.emptySet();
        return Collections.unmodifiableSet(targetIds);
    }

    public Set<ResourceId<?>> getConnectionSources(ResourceId<?> targetId) {
        Set<ResourceId<?>> sourceIds = connectionSources.get(targetId);
        if (sourceIds == null)
            return Collections.emptySet();
        return Collections.unmodifiableSet(sourceIds);
    }

    @SuppressWarnings("unchecked")
    public <R extends Resource> Connector<R> getConnector(Class<R> sourceType, ResourceId<?> targetId) {
        return (Connector<R>) connectors.get(targetId).get(sourceType);
    }

    public void resourceActive(ResourceId<?> resourceId, boolean active) {
        resourceActivations.put(resourceId, active);
    }

    public boolean isResourceActive(ResourceId<?> resourceId) {
        Boolean active = resourceActivations.get(resourceId);
        return active == null ? false : active;
    }
}
