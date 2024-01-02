package org.carthageking.mc.mcck.core.httpclient;

/*-
 * #%L
 * mcck-core-httpclient
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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpClientHelperResultTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test_getBody() {
		String body = null;
		HttpClientHelperResult<String> testObj = new HttpClientHelperResult<>(null, null, body);
		Assertions.assertEquals(true, testObj.getBody().isEmpty());
		Assertions.assertEquals(null, testObj.getBodyAsString());

		body = "xy";
		testObj = new HttpClientHelperResult<>(null, null, body);
		Assertions.assertEquals(false, testObj.getBody().isEmpty());
		Assertions.assertEquals("xy", testObj.getBodyAsString());
	}
}
