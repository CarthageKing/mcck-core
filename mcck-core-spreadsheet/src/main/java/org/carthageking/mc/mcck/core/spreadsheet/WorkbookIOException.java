package org.carthageking.mc.mcck.core.spreadsheet;

public class WorkbookIOException extends RuntimeException {

	private static final long serialVersionUID = -3724685274381239709L;

	public WorkbookIOException() {
		// noop
	}

	public WorkbookIOException(String msg) {
		super(msg);
	}

	public WorkbookIOException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
