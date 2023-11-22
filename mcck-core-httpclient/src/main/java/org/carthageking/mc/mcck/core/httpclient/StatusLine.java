package org.carthageking.mc.mcck.core.httpclient;

public class StatusLine {

	private final int code;
	private final String message;

	public StatusLine(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
