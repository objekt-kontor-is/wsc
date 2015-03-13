package de.objektkontor.wsc.container;


public interface Endpoint extends Resource {

    public abstract int port();

    public abstract Pipeline pipeline();

}
