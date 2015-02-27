package de.objektkontor.wsc.container.core;

import de.objektkontor.wsc.container.Endpoint;
import de.objektkontor.wsc.container.Processor;
import de.objektkontor.wsc.container.core.validator.EndpointValidator;
import de.objektkontor.wsc.container.core.validator.ProcessorValidator;

public class ValidatorFactory {

    public Validator<?> create(Class<?> resourceType) {

        if (resourceType == Endpoint.class)
            return new EndpointValidator();

        if (resourceType == Processor.class)
            return new ProcessorValidator();

        return null;
    }
}
