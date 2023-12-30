package org.carthageking.mc.mcck.core.httpclient.netty;

import java.util.Objects;

class HostAndPort implements Comparable<HostAndPort> {
	private String host;
	private int port;

	public HostAndPort(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public int compareTo(HostAndPort o) {
		int cmp = host.compareTo(o.host);
		if (0 == cmp) {
			cmp = Integer.compare(port, o.port);
		}
		return cmp;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	@Override
	public int hashCode() {
		return Objects.hash(host, port);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		HostAndPort other = (HostAndPort) obj;
		return Objects.equals(host, other.host) && port == other.port;
	}
}