package org.carthageking.mc.mcck.core.httpclient.okhttp3;

import org.apache.http.client.utils.URIBuilder;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import okhttp3.OkHttpClient;

class OkHttp3HttpClientHelperTest {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OkHttp3HttpClientHelperTest.class);

	private WireMockServer wireMockServer;
	private int port;
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
		wireMockServer = new WireMockServer(0);
		wireMockServer.start();
		port = wireMockServer.port();
		log.trace("Started wiremock on port {}", port);
	}

	@AfterEach
	void tearDown() throws Exception {
		wireMockServer.stop();
	}

	@Test
	void test_doGet() {
		HttpClientHelper hch = new OkHttp3HttpClientHelper(httpClient);
		Assertions.assertNotNull(((OkHttp3HttpClientHelper) hch).getHttpClient());
		HttpClientHelperResult<String> result = null;

		// basic call
		{
			wireMockServer.stubFor(WireMock.get("/sampleGet").willReturn(WireMock.status(202).withBody("yahello")));

			result = hch.doGet(HttpClientHelper.createURI(() -> {
				URIBuilder ub = new URIBuilder("http://localhost:" + port + "/sampleGet");
				return ub.build();
			}));
			log.trace("Got {} [{}] response string with length of {}",
				result.getStatusLine().getCode(), result.getStatusLine().getMessage(),
				result.getBodyAsString().length());
			Assertions.assertEquals(true, result.getBody().isPresent());
			Assertions.assertEquals(202, result.getStatusLine().getCode());

			wireMockServer.resetAll();
		}

		// testing with setting custom headers
		{
			String header1 = "x-abc123";
			String header2 = "x-xyz456";
			wireMockServer.stubFor(WireMock.get("/sampleGet")
				.withHeader(header1, WireMock.equalTo("HHH"))
				.withHeader(header2, WireMock.equalTo("JJJ"))
				.willReturn(WireMock.status(207).withBody("cognac")));

			result = hch.doGet(HttpClientHelper.createURI(() -> {
				URIBuilder ub = new URIBuilder("http://localhost:" + port + "/sampleGet");
				return ub.build();
			}), hdrs -> {
				hdrs.setHeader(header1, "HHH");
				hdrs.addHeader(header2, "JJJ");
			});
			log.trace("Got {} [{}] response string with length of {}",
				result.getStatusLine().getCode(), result.getStatusLine().getMessage(),
				result.getBodyAsString().length());
			Assertions.assertEquals(true, result.getBody().isPresent());
			Assertions.assertEquals(207, result.getStatusLine().getCode());

			wireMockServer.resetAll();
		}
	}
}
