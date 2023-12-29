package org.carthageking.mc.mcck.core.jse;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class McckUriUtil {

	private static final Map<String, Integer> SCHEME_DEFAULT_PORTS;

	// https://oeis.org/wiki/URL
	// for now, limited only to HTTP/HTTPS
	static {
		SCHEME_DEFAULT_PORTS = new HashMap<>();
		SCHEME_DEFAULT_PORTS.put("http", 80);
		SCHEME_DEFAULT_PORTS.put("https", 443);
	}

	private McckUriUtil() {
		// noop
	}

	public static int getPort(URI uri) {
		int port = uri.getPort();
		if (-1 == port) {
			String scheme = uri.getScheme().toLowerCase();
			Integer p = SCHEME_DEFAULT_PORTS.get(scheme);
			if (null == p) {
				throw new McckException("Unsupported URL scheme: " + scheme);
			}
			port = p;
		}
		return port;
	}
}
