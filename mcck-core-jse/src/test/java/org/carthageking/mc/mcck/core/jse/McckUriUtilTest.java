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

import java.net.URI;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class McckUriUtilTest {

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
	void test_URI() throws Exception {
		URI testObj = null;

		testObj = new URI("http://localhost/a/bifold?x=y");
		Assertions.assertEquals(null, testObj.getUserInfo());
		Assertions.assertEquals("localhost", testObj.getHost());
		Assertions.assertEquals(-1, testObj.getPort());
		Assertions.assertEquals(80, McckUriUtil.getPort(testObj));
		Assertions.assertEquals("/a/bifold", testObj.getPath());
		Assertions.assertEquals("x=y", testObj.getQuery());

		testObj = new URI("http://www.yahoo.com:834/a/bifold?x=y");
		Assertions.assertEquals(null, testObj.getUserInfo());
		Assertions.assertEquals("www.yahoo.com", testObj.getHost());
		Assertions.assertEquals(834, testObj.getPort());
		Assertions.assertEquals(834, McckUriUtil.getPort(testObj));
		Assertions.assertEquals("/a/bifold", testObj.getPath());
		Assertions.assertEquals("x=y", testObj.getQuery());

		testObj = new URI("https://example.org/a/bifold?x=y");
		Assertions.assertEquals(null, testObj.getUserInfo());
		Assertions.assertEquals("example.org", testObj.getHost());
		Assertions.assertEquals(-1, testObj.getPort());
		Assertions.assertEquals(443, McckUriUtil.getPort(testObj));
		Assertions.assertEquals("/a/bifold", testObj.getPath());
		Assertions.assertEquals("x=y", testObj.getQuery());

		testObj = new URI("https://192.168.0.222:278/a/bifold?x:Patient=y");
		Assertions.assertEquals(null, testObj.getUserInfo());
		Assertions.assertEquals("192.168.0.222", testObj.getHost());
		Assertions.assertEquals(278, testObj.getPort());
		Assertions.assertEquals(278, McckUriUtil.getPort(testObj));
		Assertions.assertEquals("/a/bifold", testObj.getPath());
		Assertions.assertEquals("x:Patient=y", testObj.getQuery());

		testObj = new URI("https://user:user@192.168.0.222:278/a/bifold?x:Patient=y");
		Assertions.assertEquals("https", testObj.getScheme());
		Assertions.assertEquals("user:user", testObj.getUserInfo());
		Assertions.assertEquals("192.168.0.222", testObj.getHost());
		Assertions.assertEquals(278, testObj.getPort());
		Assertions.assertEquals(278, McckUriUtil.getPort(testObj));
		Assertions.assertEquals("/a/bifold", testObj.getPath());
		Assertions.assertEquals("x:Patient=y", testObj.getQuery());

		// unsupported port
		try {
			McckUriUtil.getPort(new URI("ftp://my.com/file"));
			Assertions.fail("did not throw expected exception");
		} catch (McckException e) {
			Assertions.assertEquals(true, e.getMessage().contains("Unsupported URL scheme"));
		}
	}
}
