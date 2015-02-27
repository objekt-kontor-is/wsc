package de.objektkontor.wsc.container;



public interface Dispatcher<K, M, S extends Selector> extends Resource {

    public final static String DISPATCHER_ID_PROPERTY = "connectorId";

    public abstract K identify(M message) throws Exception;

    public abstract Pipeline dispatch(K key);

    public abstract Handler defaultHadler();
}
