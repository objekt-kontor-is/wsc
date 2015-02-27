package de.objektkontor.wsc.container.core.validator;

import de.objektkontor.wsc.container.Endpoint;
import de.objektkontor.wsc.container.core.Validator;

public class EndpointValidator implements Validator<Endpoint> {

    @Override
    public void validate(Endpoint endpoint) throws Exception {
        endpoint.pipeline().validate();
    }
}
