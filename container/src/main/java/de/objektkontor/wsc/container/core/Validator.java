package de.objektkontor.wsc.container.core;

import de.objektkontor.wsc.container.Resource;

public interface Validator<R extends Resource> {

    public abstract void validate(R resource) throws Exception;
}
