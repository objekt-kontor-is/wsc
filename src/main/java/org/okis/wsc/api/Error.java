package org.okis.wsc.api;

import io.netty.handler.codec.http.HttpResponseStatus;

public class Error extends Exception {

    private static final long serialVersionUID = -2230954857135308661L;

    private final HttpResponseStatus code;

    public Error(HttpResponseStatus code) {
        this.code = code;
    }

    public Error(HttpResponseStatus code, String message) {
        super(message);
        this.code = code;
    }

    public HttpResponseStatus getCode() {
        return code;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
