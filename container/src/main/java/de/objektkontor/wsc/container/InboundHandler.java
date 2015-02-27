package de.objektkontor.wsc.container;


public interface InboundHandler extends Handler {

    public abstract Class<?> inputInboundType();

    public abstract Class<?> outputInboundType();

}
