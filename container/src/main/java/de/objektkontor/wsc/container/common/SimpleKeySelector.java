package de.objektkontor.wsc.container.common;

import de.objektkontor.wsc.container.Selector;

public class SimpleKeySelector<K> implements Selector {

    private final K key;

    public SimpleKeySelector(K key) {
        this.key = key;
    }

    public K key() {
        return key;
    }
}
