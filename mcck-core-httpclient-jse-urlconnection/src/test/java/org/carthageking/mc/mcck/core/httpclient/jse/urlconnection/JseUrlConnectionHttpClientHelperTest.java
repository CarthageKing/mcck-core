package org.carthageking.mc.mcck.core.httpclient.jse.urlconnection;

import java.net.ProtocolException;

import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperException;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperTestBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JseUrlConnectionHttpClientHelperTest extends HttpClientHelperTestBase {

	//private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JseUrlConnectionHttpClientHelperTest.class);

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		setUpBase();
	}

	@AfterEach
	void tearDown() throws Exception {
		tearDownBase();
	}

	@Test
	void test_doGet() {
		HttpClientHelper hch = new JseUrlConnectionHttpClientHelper();
		test_doGetBase(hch);
	}

	@Test
	void test_doDelete() {
		HttpClientHelper hch = new JseUrlConnectionHttpClientHelper();
		test_doDeleteBase(hch);
	}

	@Test
	void test_doPost() {
		HttpClientHelper hch = new JseUrlConnectionHttpClientHelper();
		test_doPostBase(hch);
	}

	@Test
	void test_doPut() {
		HttpClientHelper hch = new JseUrlConnectionHttpClientHelper();
		test_doPutBase(hch);
	}

	@Test
	void test_doPatch() {
		HttpClientHelper hch = new JseUrlConnectionHttpClientHelper();
		try {
			test_doPatchBase(hch);
			Assertions.fail("did not throw expected exception");
		} catch (HttpClientHelperException e) {
			ProtocolException ex = (ProtocolException) e.getCause();
			Assertions.assertEquals("Invalid HTTP method: PATCH", ex.getMessage());
		}
	}
}
