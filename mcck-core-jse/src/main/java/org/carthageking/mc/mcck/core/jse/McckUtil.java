package org.carthageking.mc.mcck.core.jse;

import java.util.Collection;
import java.util.Map;

public final class McckUtil {

	private McckUtil() {
		// noop
	}

	public static boolean isNullOrEmpty(Collection<?> c) {
		return (null == c || c.isEmpty());
	}

	public static boolean isNullOrEmpty(Map<?, ?> m) {
		return (null == m || m.isEmpty());
	}
}
