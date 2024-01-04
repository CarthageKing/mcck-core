package org.carthageking.mc.mcck.core.json;

/*-
 * #%L
 * mcck-core-json
 * %%
 * Copyright (C) 2024 Michael I. Calderero
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class McckJsonUtil {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
		.registerModule(new JavaTimeModule());

	private McckJsonUtil() {
		// noop
	}

	public static ObjectMapper getMapperInstance() {
		return OBJECT_MAPPER;
	}

	public static JsonNode toJsonNode(String str) {
		try {
			return OBJECT_MAPPER.readTree(str);
		} catch (JsonProcessingException e) {
			throw new McckJsonException(e);
		}
	}

	public static <T> T toObject(String str, Class<T> clazz) {
		try {
			return OBJECT_MAPPER.readValue(str, clazz);
		} catch (JsonProcessingException e) {
			throw new McckJsonException(e);
		}
	}

	public static String toStr(JsonNode jn) {
		try {
			return OBJECT_MAPPER.writeValueAsString(jn);
		} catch (JsonProcessingException e) {
			throw new McckJsonException(e);
		}
	}

	public static String toStr(Object jn) {
		try {
			return OBJECT_MAPPER.writeValueAsString(jn);
		} catch (JsonProcessingException e) {
			throw new McckJsonException(e);
		}
	}
}
