package org.okis.wsc.api;

import java.util.Arrays;

public class URLTarget {

	private final String [] urlPrefixes;

	public URLTarget(String ... urlPrefix) {
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
