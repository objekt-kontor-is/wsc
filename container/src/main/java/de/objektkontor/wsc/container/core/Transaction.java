package de.objektkontor.wsc.container.core;


public abstract class Transaction {

    protected abstract void prepare(TransactionContent content);

}
