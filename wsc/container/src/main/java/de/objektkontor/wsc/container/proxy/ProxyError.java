package de.objektkontor.wsc.container.proxy;

public class ProxyError extends Exception {

    private static final long serialVersionUID = 6543624932312348402L;

    public ProxyError() {
        super();
    }

    public ProxyError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ProxyError(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyError(String message) {
        super(message);
    }

    public ProxyError(Throwable cause) {
        super(cause);
    }
}
