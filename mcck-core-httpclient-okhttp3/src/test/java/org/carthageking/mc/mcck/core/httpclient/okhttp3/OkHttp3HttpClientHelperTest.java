package org.carthageking.mc.mcck.core.httpclient.okhttp3;

import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperTestBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;

class OkHttp3HttpClientHelperTest extends HttpClientHelperTestBase {

	//private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OkHttp3HttpClientHelperTest.class);

	private OkHttpClient httpClient;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		httpClient = new OkHttpClient();
		setUpBase();
	}

	@AfterEach
	void tearDown() throws Exception {
		tearDownBase();
	}

	@Test
	void test_doGet() {
		HttpClientHelper hch = new OkHttp3HttpClientHelper(httpClient);
		Assertions.assertNotNull(((OkHttp3HttpClientHelper) hch).getHttpClient());
		test_doGetBase(hch);
	}

	@Test
	void test_doDelete() {
		HttpClientHelper hch = new OkHttp3HttpClientHelper(httpClient);
		Assertions.assertNotNull(((OkHttp3HttpClientHelper) hch).getHttpClient());
		test_doDeleteBase(hch);
	}

	@Test
	void test_doPost() {
		HttpClientHelper hch = new OkHttp3HttpClientHelper(httpClient);
		Assertions.assertNotNull(((OkHttp3HttpClientHelper) hch).getHttpClient());
		test_doPostBase(hch);
	}

	@Test
	void test_doPut() {
		HttpClientHelper hch = new OkHttp3HttpClientHelper(httpClient);
		Assertions.assertNotNull(((OkHttp3HttpClientHelper) hch).getHttpClient());
		test_doPutBase(hch);
	}

	@Test
	void test_doPatch() {
		HttpClientHelper hch = new OkHttp3HttpClientHelper(httpClient);
		Assertions.assertNotNull(((OkHttp3HttpClientHelper) hch).getHttpClient());
		test_doPatchBase(hch);
	}
}
