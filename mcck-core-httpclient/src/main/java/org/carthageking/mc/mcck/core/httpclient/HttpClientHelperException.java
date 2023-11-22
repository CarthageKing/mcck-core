package org.carthageking.mc.mcck.core.httpclient;

public class HttpClientHelperException extends RuntimeException {

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
}
