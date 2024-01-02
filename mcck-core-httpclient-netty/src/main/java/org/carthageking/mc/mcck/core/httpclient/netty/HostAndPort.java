package org.carthageking.mc.mcck.core.httpclient.netty;

/*-
 * #%L
 * mcck-core-httpclient-netty
 * %%
 * Copyright (C) 2023 - 2024 Michael I. Calderero
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
