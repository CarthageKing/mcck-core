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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class McckStrUtil {

	private McckStrUtil() {
		// noop
	}

	public static List<String> readIntoLines(String str) {
		List<String> lst = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new StringReader(str))) {
			String line = null;
			while (null != (line = br.readLine())) {
				lst.add(line);
			}
		} catch (IOException e) {
			throw new McckException(e);
		}
		return lst;
	}
}
