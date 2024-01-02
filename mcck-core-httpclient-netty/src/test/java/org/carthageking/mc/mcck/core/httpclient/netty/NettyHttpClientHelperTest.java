package org.carthageking.mc.mcck.core.httpclient.netty;

/*-
 * #%L
 * mcck-core-httpclient-netty
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

import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperTestBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.netty.bootstrap.Bootstrap;

class NettyHttpClientHelperTest extends HttpClientHelperTestBase {

	//private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NettyHttpClientHelperTest.class);

	private Bootstrap bootstrap;
	private NettyHttpClientHelper hch;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		bootstrap = new Bootstrap();
		setUpBase();
	}

	@AfterEach
	void tearDown() throws Exception {
		tearDownBase();
		hch.close();
	}

	@Test
	void test_doGet() {
		hch = new NettyHttpClientHelper(bootstrap);
		test_doGetBase(hch);
	}

	@Test
	void test_doDelete() {
		hch = new NettyHttpClientHelper(bootstrap);
		test_doDeleteBase(hch);
	}

	@Test
	void test_doPost() {
		hch = new NettyHttpClientHelper(bootstrap);
		test_doPostBase(hch);
	}

	@Test
	void test_doPut() {
		hch = new NettyHttpClientHelper(bootstrap);
		test_doPutBase(hch);
	}

	@Test
	void test_doPatch() {
		hch = new NettyHttpClientHelper(bootstrap);
		test_doPatchBase(hch);
	}
}
