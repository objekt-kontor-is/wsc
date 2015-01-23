package org.okis.wsc;

import io.netty.handler.codec.http.FullHttpRequest;

import org.okis.wsc.api.URLTarget;
import org.okis.wsc.api.Handler;

public class Resolver {

	private Handler defaultHandler;

	public void registerHandler(URLTarget enpoint, Handler handler) {
		defaultHandler = handler;
	}

	public Handler getHandler(FullHttpRequest request) {
		return defaultHandler;
	}
}
