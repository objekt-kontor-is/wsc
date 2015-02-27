package de.objektkontor.wsc.container.core.validator;

import de.objektkontor.wsc.container.Processor;
import de.objektkontor.wsc.container.core.Validator;

public class ProcessorValidator implements Validator<Processor<?>> {

    @Override
    public void validate(Processor<?> processor) throws Exception {
        processor.pipeline().validate();
    }
}
