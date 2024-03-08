package org.carthageking.mc.mcck.core.httpclient.jetty;

/*-
 * #%L
 * mcck-core-httpclient-jetty
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

import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperTestBase;
import org.eclipse.jetty.client.HttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JettyHttpClientHelperTest extends HttpClientHelperTestBase {

	//private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JettyHttpClientHelperTest.class);

	private HttpClient httpClient;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		httpClient = new HttpClient();
		httpClient.start();
		setUpBase();
	}

	@AfterEach
	void tearDown() throws Exception {
		tearDownBase();
		httpClient.stop();
	}

	@Test
	void test_doGet() {
		HttpClientHelper hch = new JettyHttpClientHelper(httpClient);
		Assertions.assertNotNull(((JettyHttpClientHelper) hch).getHttpClient());
		test_doGetBase(hch);
	}

	@Test
	void test_doDelete() {
		HttpClientHelper hch = new JettyHttpClientHelper(httpClient);
		Assertions.assertNotNull(((JettyHttpClientHelper) hch).getHttpClient());
		test_doDeleteBase(hch);
	}

	@Test
	void test_doPost() {
		HttpClientHelper hch = new JettyHttpClientHelper(httpClient);
		Assertions.assertNotNull(((JettyHttpClientHelper) hch).getHttpClient());
		test_doPostBase(hch);
	}

	@Test
	void test_doPut() {
		HttpClientHelper hch = new JettyHttpClientHelper(httpClient);
		Assertions.assertNotNull(((JettyHttpClientHelper) hch).getHttpClient());
		test_doPutBase(hch);
	}

	@Test
	void test_doPatch() {
		HttpClientHelper hch = new JettyHttpClientHelper(httpClient);
		Assertions.assertNotNull(((JettyHttpClientHelper) hch).getHttpClient());
		test_doPatchBase(hch);
	}
}
