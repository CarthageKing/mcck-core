package org.carthageking.mc.mcck.core.httpclient;

import org.carthageking.mc.mcck.core.jse.McckException;

public class HttpClientHelperException extends McckException {

	private static final long serialVersionUID = -1380292491906255088L;

	public HttpClientHelperException() {
		// noop
	}

	public HttpClientHelperException(String msg) {
		super(msg);
	}

	public HttpClientHelperException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public HttpClientHelperException(Throwable cause) {
		super(cause);
	}
}
