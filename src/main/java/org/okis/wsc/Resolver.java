package org.okis.wsc;

import io.netty.handler.codec.http.FullHttpRequest;

import org.okis.wsc.api.Endpoint;
import org.okis.wsc.api.Handler;

public class Resolver {

	private Handler defaultHandler;

	public void registerHandler(Endpoint enpoint, Handler handler) {
		defaultHandler = handler;
	}

	public Handler getHandler(FullHttpRequest request) {
		return defaultHandler;
	}
}
