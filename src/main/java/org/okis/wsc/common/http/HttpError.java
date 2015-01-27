package org.okis.wsc.common.http;

import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpError extends Exception {

    private static final long serialVersionUID = -2230954857135308661L;

    private final HttpResponseStatus code;

    public HttpError(HttpResponseStatus code) {
        this.code = code;
    }

    public HttpError(HttpResponseStatus code, String message) {
        super(message);
        this.code = code;
    }

    public HttpError(HttpResponseStatus code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public HttpError(HttpResponseStatus code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public HttpResponseStatus getCode() {
        return code;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

	@Override
	public String toString() {
		String response = "HttpResponse [code=" + code + "]";
        String message = getLocalizedMessage();
        return message != null ? response + ": " + message : response;
	}
}
