package org.carthageking.mc.mcck.core.httpclient.apache.httpclient4;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperTestBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApacheHttpClient4HttpClientHelperTest extends HttpClientHelperTestBase {

	//private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApacheHttpClient4HttpClientHelperTest.class);

	private CloseableHttpClient httpClient;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		httpClient = HttpClientBuilder.create().build();
		setUpBase();
	}

	@AfterEach
	void tearDown() throws Exception {
		tearDownBase();
		httpClient.close();
	}

	@Test
	void test_doGet() {
		HttpClientHelper hch = new ApacheHttpClient4HttpClientHelper(httpClient);
		Assertions.assertNotNull(((ApacheHttpClient4HttpClientHelper) hch).getHttpClient());
		test_doGetBase(hch);
	}

	@Test
	void test_doDelete() {
		HttpClientHelper hch = new ApacheHttpClient4HttpClientHelper(httpClient);
		Assertions.assertNotNull(((ApacheHttpClient4HttpClientHelper) hch).getHttpClient());
		test_doDeleteBase(hch);
	}

	@Test
	void test_doPost() {
		HttpClientHelper hch = new ApacheHttpClient4HttpClientHelper(httpClient);
		Assertions.assertNotNull(((ApacheHttpClient4HttpClientHelper) hch).getHttpClient());
		test_doPostBase(hch);
	}

	@Test
	void test_doPut() {
		HttpClientHelper hch = new ApacheHttpClient4HttpClientHelper(httpClient);
		Assertions.assertNotNull(((ApacheHttpClient4HttpClientHelper) hch).getHttpClient());
		test_doPutBase(hch);
	}

	@Test
	void test_doPatch() {
		HttpClientHelper hch = new ApacheHttpClient4HttpClientHelper(httpClient);
		Assertions.assertNotNull(((ApacheHttpClient4HttpClientHelper) hch).getHttpClient());
		test_doPatchBase(hch);
	}
}
