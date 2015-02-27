package de.objektkontor.wsc.container.core;


public abstract class RepositoryOperation {

    protected abstract void execute(Repository repository) throws Exception;

    protected abstract void revert(Repository repository) throws Exception;
}
