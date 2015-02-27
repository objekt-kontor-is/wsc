package de.objektkontor.wsc.container;


public interface Processor<S extends Selector> extends Resource {

    public abstract S selector();

    public abstract Pipeline pipeline();

}
