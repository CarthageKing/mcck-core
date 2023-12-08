package org.carthageking.mc.mcck.core.spreadsheet;

import org.carthageking.mc.mcck.core.jse.McckException;

public class WorkbookIOException extends McckException {

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

	public WorkbookIOException(Throwable cause) {
		super(cause);
	}
}
