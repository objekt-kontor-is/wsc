package org.okis.wsc.api;

import java.util.Arrays;

public class Endpoint {

	private final String [] urlPrefixes;

	public Endpoint(String ... urlPrefix) {
		urlPrefixes = urlPrefix;
	}

	public String [] getUrlPrefixes() {
		return urlPrefixes;
	}

	@Override
	public String toString() {
		return "Endpoint [urlPrefixes=" + Arrays.toString(urlPrefixes) + "]";
	}
}
