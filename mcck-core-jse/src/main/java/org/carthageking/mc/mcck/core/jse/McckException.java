package org.carthageking.mc.mcck.core.jse;

public class McckException extends RuntimeException {

	private static final long serialVersionUID = -1380292491906255088L;

	public McckException() {
		// noop
	}

	public McckException(String msg) {
		super(msg);
	}

	public McckException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public McckException(Throwable cause) {
		super(cause);
	}
}
