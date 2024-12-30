package org.carthageking.mc.mcck.core.EXAMPLES.sbtm.controller.model;

public class BasicLoginInfoForm implements java.io.Serializable {

	private static final long serialVersionUID = -4718116184665703438L;

	private String username;
	private String password;

	public BasicLoginInfoForm() {
		// noop
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
