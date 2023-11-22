package org.carthageking.mc.mcck.core.httpclient;

import java.net.URI;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpClientHelperTest {

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
	void test_createURI() {
		URI result = null;

		result = HttpClientHelper.createURI(() -> new URI("http://www.yahoo.com"));
		Assertions.assertEquals("http://www.yahoo.com", result.toString());

		try {
			HttpClientHelper.createURI(() -> {
				throw new UnsupportedOperationException("no impl");
			});
		} catch (HttpClientHelperException e) {
			UnsupportedOperationException ex = (UnsupportedOperationException) e.getCause();
			Assertions.assertEquals("no impl", ex.getMessage());
		}
	}

}
