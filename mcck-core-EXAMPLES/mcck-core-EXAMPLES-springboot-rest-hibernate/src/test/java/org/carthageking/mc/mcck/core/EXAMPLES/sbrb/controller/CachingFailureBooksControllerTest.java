package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/*-
 * #%L
 * mcck-core-EXAMPLES-springboot-rest-hibernate
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

import org.springframework.test.context.TestPropertySource;

@DisplayName("Books: CRUD API")
// in this test, we set a cache type that we don't have running. the goal of this
// test is to ensure the application still works even when the cache is down
@TestPropertySource(properties = {
	"spring.cache.type=redis",
	"spring.data.redis.port=6377",
})
class CachingFailureBooksControllerTest extends BooksControllerTestBase {

	@Test
	@DisplayName("Caching Enabled But Cache Server Cannot Be Accessed")
	@Override
	void test_bookCRUD() {
		super.test_bookCRUD();
	}
}
