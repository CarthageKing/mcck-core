package org.carthageking.mc.mcck.core.httpclient;

/*-
 * #%L
 * mcck-core-httpclient-commontest
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

import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Assertions;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

public class HttpClientHelperTestBase {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(HttpClientHelperTestBase.class);

	protected WireMockServer wireMockServer;
	protected int port;

	protected void setUpBase() throws Exception {
		wireMockServer = new WireMockServer(0);
		wireMockServer.start();
		port = wireMockServer.port();
		LOG.trace("Started wiremock on port {}", port);
	}

	protected void tearDownBase() throws Exception {
		wireMockServer.stop();
	}

	protected void test_doGetBase(HttpClientHelper hch) {
		HttpClientHelperResult<String> result = null;

		// basic call
		{
			wireMockServer.stubFor(WireMock.get("/sampleGet").willReturn(WireMock.status(202).withBody("yahello")));

			result = hch.doGet(HttpClientHelper.createURI(() -> {
				URIBuilder ub = new URIBuilder("http://localhost:" + port + "/sampleGet");
				return ub.build();
			}));
			LOG.trace("Got {} [{}] response string with length of {}",
				result.getStatusLine().getCode(), result.getStatusLine().getMessage(),
				result.getBodyAsString().length());
			Assertions.assertEquals(true, result.getBody().isPresent());
			Assertions.assertEquals("yahello", result.getBodyAsString());
			Assertions.assertEquals(202, result.getStatusLine().getCode());

			wireMockServer.resetAll();
		}
	}

	protected void test_doDeleteBase(HttpClientHelper hch) {
		HttpClientHelperResult<String> result = null;

		// basic call
		{
			wireMockServer.stubFor(WireMock.delete("/sampleDelete").willReturn(WireMock.status(204).withBody("yahello")));

			result = hch.doDelete(HttpClientHelper.createURI(() -> {
				URIBuilder ub = new URIBuilder("http://localhost:" + port + "/sampleDelete");
				return ub.build();
			}));
			LOG.trace("Got {} [{}] response string with length of {}",
				result.getStatusLine().getCode(), result.getStatusLine().getMessage(),
				result.getBodyAsString().length());
			Assertions.assertEquals(true, result.getBody().isPresent());
			Assertions.assertEquals("", result.getBodyAsString());
			Assertions.assertEquals(204, result.getStatusLine().getCode());

			wireMockServer.resetAll();
		}

		// testing with setting custom headers
		{
			String header1 = "x-abc123";
			String header2 = "x-xyz456";
			wireMockServer.stubFor(WireMock.delete("/sampleDeleted")
				.withHeader(header1, WireMock.equalTo("HHH"))
				.withHeader(header2, WireMock.equalTo("JJJ"))
				.willReturn(WireMock.status(212).withBody("cognac")));

			result = hch.doDelete(HttpClientHelper.createURI(() -> {
				URIBuilder ub = new URIBuilder("http://localhost:" + port + "/sampleDeleted");
				return ub.build();
			}), hdrs -> {
				hdrs.setHeader(header1, "HHH");
				hdrs.addHeader(header2, "JJJ");
			});
			LOG.trace("Got {} [{}] response string with length of {}",
				result.getStatusLine().getCode(), result.getStatusLine().getMessage(),
				result.getBodyAsString().length());
			Assertions.assertEquals(true, result.getBody().isPresent());
			Assertions.assertEquals("cognac", result.getBodyAsString());
			Assertions.assertEquals(212, result.getStatusLine().getCode());

			wireMockServer.resetAll();
		}
	}

	protected void test_doPostBase(HttpClientHelper hch) {
		HttpClientHelperResult<String> result = null;

		// basic call
		{
			wireMockServer.stubFor(WireMock.post("/samplePost").withRequestBody(WireMock.equalTo("included")).willReturn(WireMock.status(202).withBody("yahello")));

			result = hch.doPost(HttpClientHelper.createURI(() -> {
				URIBuilder ub = new URIBuilder("http://localhost:" + port + "/samplePost");
				return ub.build();
			}), "included");
			LOG.trace("Got {} [{}] response string with length of {}",
				result.getStatusLine().getCode(), result.getStatusLine().getMessage(),
				result.getBodyAsString().length());
			Assertions.assertEquals(true, result.getBody().isPresent());
			Assertions.assertEquals("yahello", result.getBodyAsString());
			Assertions.assertEquals(202, result.getStatusLine().getCode());

			wireMockServer.resetAll();
		}

		// testing with setting custom headers
		{
			String header1 = "x-abc123";
			String header2 = "x-xyz456";
			wireMockServer.stubFor(WireMock.post("/samplePost")
				.withHeader(header1, WireMock.equalTo("HHH"))
				.withHeader(header2, WireMock.equalTo("JJJ"))
				.withRequestBody(WireMock.equalTo("lasted"))
				.willReturn(WireMock.status(207).withBody("cognac").withHeader("X-returned", "returnok")));

			result = hch.doPost(HttpClientHelper.createURI(() -> {
				URIBuilder ub = new URIBuilder("http://localhost:" + port + "/samplePost");
				return ub.build();
			}), "lasted", hdrs -> {
				hdrs.setHeader(header1, "HHH");
				hdrs.addHeader(header2, "JJJ");
			});
			LOG.trace("Got {} [{}] response string with length of {}",
				result.getStatusLine().getCode(), result.getStatusLine().getMessage(),
				result.getBodyAsString().length());
			Assertions.assertEquals(true, result.getBody().isPresent());
			Assertions.assertEquals("cognac", result.getBodyAsString());
			Assertions.assertEquals(207, result.getStatusLine().getCode());
			Assertions.assertEquals(1, result.getHeaders().get("X-returned").size());
			Assertions.assertEquals("returnok", result.getHeaders().get("X-returned").get(0));

			wireMockServer.resetAll();
		}
	}

	protected void test_doPutBase(HttpClientHelper hch) {
		HttpClientHelperResult<String> result = null;

		// basic call
		{
			wireMockServer.stubFor(WireMock.put("/samplePut").withRequestBody(WireMock.equalTo("included")).willReturn(WireMock.status(209).withBody("yahello")));

			result = hch.doPut(HttpClientHelper.createURI(() -> {
				URIBuilder ub = new URIBuilder("http://localhost:" + port + "/samplePut");
				return ub.build();
			}), "included");
			LOG.trace("Got {} [{}] response string with length of {}",
				result.getStatusLine().getCode(), result.getStatusLine().getMessage(),
				result.getBodyAsString().length());
			Assertions.assertEquals(true, result.getBody().isPresent());
			Assertions.assertEquals("yahello", result.getBodyAsString());
			Assertions.assertEquals(209, result.getStatusLine().getCode());

			wireMockServer.resetAll();
		}

		// testing with setting custom headers
		{
			String header1 = "x-abc123";
			String header2 = "x-xyz456";
			wireMockServer.stubFor(WireMock.put("/samplePute")
				.withHeader(header1, WireMock.equalTo("HHH"))
				.withHeader(header2, WireMock.equalTo("JJJ"))
				.withRequestBody(WireMock.equalTo("lasted"))
				.willReturn(WireMock.status(203).withBody("cognac").withHeader("X-returned", "returnok")));

			result = hch.doPut(HttpClientHelper.createURI(() -> {
				URIBuilder ub = new URIBuilder("http://localhost:" + port + "/samplePute");
				return ub.build();
			}), "lasted", hdrs -> {
				hdrs.setHeader(header1, "HHH");
				hdrs.addHeader(header2, "JJJ");
			});
			LOG.trace("Got {} [{}] response string with length of {}",
				result.getStatusLine().getCode(), result.getStatusLine().getMessage(),
				result.getBodyAsString().length());
			Assertions.assertEquals(true, result.getBody().isPresent());
			Assertions.assertEquals("cognac", result.getBodyAsString());
			Assertions.assertEquals(203, result.getStatusLine().getCode());
			Assertions.assertEquals(1, result.getHeaders().get("X-returned").size());
			Assertions.assertEquals("returnok", result.getHeaders().get("X-returned").get(0));

			wireMockServer.resetAll();
		}
	}

	protected void test_doPatchBase(HttpClientHelper hch) {
		HttpClientHelperResult<String> result = null;

		// basic call
		{
			wireMockServer.stubFor(WireMock.patch("/samplePatch").withRequestBody(WireMock.equalTo("included")).willReturn(WireMock.status(213).withBody("yahello")));

			result = hch.doPatch(HttpClientHelper.createURI(() -> {
				URIBuilder ub = new URIBuilder("http://localhost:" + port + "/samplePatch");
				return ub.build();
			}), "included");
			LOG.trace("Got {} [{}] response string with length of {}",
				result.getStatusLine().getCode(), result.getStatusLine().getMessage(),
				result.getBodyAsString().length());
			Assertions.assertEquals(true, result.getBody().isPresent());
			Assertions.assertEquals("yahello", result.getBodyAsString());
			Assertions.assertEquals(213, result.getStatusLine().getCode());

			wireMockServer.resetAll();
		}

		// testing with setting custom headers
		{
			String header1 = "x-abc123";
			String header2 = "x-xyz456";
			wireMockServer.stubFor(WireMock.patch("/samplePatchd")
				.withHeader(header1, WireMock.equalTo("HHH"))
				.withHeader(header2, WireMock.equalTo("JJJ"))
				.withRequestBody(WireMock.equalTo("lasted"))
				.willReturn(WireMock.status(223).withBody("cognac").withHeader("X-returned", "returnok")));

			result = hch.doPatch(HttpClientHelper.createURI(() -> {
				URIBuilder ub = new URIBuilder("http://localhost:" + port + "/samplePatchd");
				return ub.build();
			}), "lasted", hdrs -> {
				hdrs.setHeader(header1, "HHH");
				hdrs.addHeader(header2, "JJJ");
			});
			LOG.trace("Got {} [{}] response string with length of {}",
				result.getStatusLine().getCode(), result.getStatusLine().getMessage(),
				result.getBodyAsString().length());
			Assertions.assertEquals(true, result.getBody().isPresent());
			Assertions.assertEquals("cognac", result.getBodyAsString());
			Assertions.assertEquals(223, result.getStatusLine().getCode());
			Assertions.assertEquals(1, result.getHeaders().get("X-returned").size());
			Assertions.assertEquals("returnok", result.getHeaders().get("X-returned").get(0));

			wireMockServer.resetAll();
		}
	}
}
