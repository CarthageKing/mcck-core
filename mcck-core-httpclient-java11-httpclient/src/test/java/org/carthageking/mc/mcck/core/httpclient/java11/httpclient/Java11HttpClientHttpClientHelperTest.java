package org.carthageking.mc.mcck.core.httpclient.java11.httpclient;

import java.net.http.HttpClient;

import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperTestBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Java11HttpClientHttpClientHelperTest extends HttpClientHelperTestBase {

	//private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Java11HttpClientHttpClientHelperTest.class);

	private HttpClient httpClient;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		httpClient = HttpClient.newHttpClient();
		setUpBase();
	}

	@AfterEach
	void tearDown() throws Exception {
		tearDownBase();
	}

	@Test
	void test_doGet() {
		HttpClientHelper hch = new Java11HttpClientHttpClientHelper(httpClient);
		Assertions.assertNotNull(((Java11HttpClientHttpClientHelper) hch).getHttpClient());
		test_doGetBase(hch);
	}

	@Test
	void test_doDelete() {
		HttpClientHelper hch = new Java11HttpClientHttpClientHelper(httpClient);
		Assertions.assertNotNull(((Java11HttpClientHttpClientHelper) hch).getHttpClient());
		test_doDeleteBase(hch);
	}

	@Test
	void test_doPost() {
		HttpClientHelper hch = new Java11HttpClientHttpClientHelper(httpClient);
		Assertions.assertNotNull(((Java11HttpClientHttpClientHelper) hch).getHttpClient());
		test_doPostBase(hch);
	}

	@Test
	void test_doPut() {
		HttpClientHelper hch = new Java11HttpClientHttpClientHelper(httpClient);
		Assertions.assertNotNull(((Java11HttpClientHttpClientHelper) hch).getHttpClient());
		test_doPutBase(hch);
	}

	@Test
	void test_doPatch() {
		HttpClientHelper hch = new Java11HttpClientHttpClientHelper(httpClient);
		Assertions.assertNotNull(((Java11HttpClientHttpClientHelper) hch).getHttpClient());
		test_doPatchBase(hch);
	}
}
