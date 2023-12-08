package org.carthageking.mc.mcck.core.test.junit5;

import java.util.List;

import org.junit.jupiter.api.Assertions;

public final class McckJunit5Util {

	private McckJunit5Util() {
		// noop
	}

	public static void assertSameLines(List<String> expected, List<String> actual) {
		Assertions.assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			String exp = expected.get(i);
			String act = actual.get(i);
			Assertions.assertEquals(exp, act, "mismatch at index " + i);
		}
	}
}
