package de.objektkontor.wsc.container.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.objektkontor.wsc.container.Pipeline;
import de.objektkontor.wsc.container.Processor;
import de.objektkontor.wsc.container.Selector;

public abstract class AbstractProcessor<S extends Selector> extends AbstractResource implements Processor<S> {

    private final static Logger log = LoggerFactory.getLogger(AbstractProcessor.class);

    private Pipeline pipeline;

    public AbstractProcessor(String id) {
        super(Processor.class, id);
    }

    @Override
    public Connector<?>[] init() throws Exception {
        pipeline = buildPipeline();
        return Connector.NO_CONNECTORS;
    }

    @Override
    public void start() throws Exception {
        super.start();
        log.info(id() + ": using pipeline: " + pipeline);
    }

    @Override
    public void destroy() {
        pipeline = null;
        super.destroy();
    }

    @Override
    public Pipeline pipeline() {
        return pipeline;
    }

    protected abstract Pipeline buildPipeline() throws Exception;
}
