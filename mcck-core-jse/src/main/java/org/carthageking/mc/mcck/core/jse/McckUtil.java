package org.carthageking.mc.mcck.core.jse;

/*-
 * #%L
 * mcck-core-jse
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
