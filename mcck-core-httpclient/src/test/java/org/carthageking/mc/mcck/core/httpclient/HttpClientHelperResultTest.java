package org.carthageking.mc.mcck.core.httpclient;

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
