package de.objektkontor.wsc.container;


public interface OutboundHandler extends Handler {

    public abstract Class<?> inputOutboundType();

    public abstract Class<?> outputOutboundType();

}
