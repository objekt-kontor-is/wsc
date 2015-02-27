package de.objektkontor.wsc.container.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.objektkontor.wsc.container.Pipeline;
import de.objektkontor.wsc.container.Processor;
import de.objektkontor.wsc.container.core.AbstractDispatcher;

public abstract class HashMapDispatcher<K, M> extends AbstractDispatcher<K, M, SimpleKeySelector<K>> {

    private final ConcurrentMap<K, Processor<SimpleKeySelector<K>>> processors = new ConcurrentHashMap<>();

    public HashMapDispatcher(String id) {
        super(id);
    }

    @Override
    public Pipeline dispatch(K key) {
        Processor<SimpleKeySelector<K>> processor = processors.get(key);
        return processor == null ? null : processor.pipeline();
    }

    @Override
    protected void addProcessor(Processor<SimpleKeySelector<K>> processor) throws Exception {
        SimpleKeySelector<K> selector = processor.selector();
        Processor<SimpleKeySelector<K>> registeredService = processors.putIfAbsent(selector.key(), processor);
        if (registeredService != null && ! registeredService.equals(processor))
            throw new IllegalArgumentException("Another Processor allready registered for selector: " + selector);
        processors.put(selector.key(), processor);
    }

    @Override
    protected void removeProcessor(Processor<SimpleKeySelector<K>> processor) throws Exception {
        processors.remove(processor.selector().key());
    }
}
