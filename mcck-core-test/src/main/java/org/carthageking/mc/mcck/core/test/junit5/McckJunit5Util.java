package org.carthageking.mc.mcck.core.test.junit5;

/*-
 * #%L
 * mcck-core-test
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
