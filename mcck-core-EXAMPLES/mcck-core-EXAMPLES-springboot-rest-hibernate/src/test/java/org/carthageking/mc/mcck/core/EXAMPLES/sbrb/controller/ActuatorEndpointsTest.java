package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.controller;

import org.apache.http.HttpHeaders;

/*-
 * #%L
 * mcck-core-EXAMPLES-springboot-rest-hibernate
 * %%
 * Copyright (C) 2024 Michael I. Calderero
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
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.TestDbConfig;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.TestSpringConfig;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.config.CommonConfig;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperResult;
import org.carthageking.mc.mcck.core.json.McckJsonUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import jakarta.annotation.Resource;

@ContextConfiguration(classes = { TestSpringConfig.class, CommonConfig.class, TestDbConfig.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// need the annotation below so things like /actuator/prometheus is
// configured properly in test context. the below annotation is not needed
// in the actual application
@AutoConfigureObservability
class ActuatorEndpointsTest {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ActuatorEndpointsTest.class);

	@Resource
	private HttpClientHelper httpClientHelper;

	@LocalServerPort
	private int port;

	private String baseUrl;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		baseUrl = "http://localhost:" + port;
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test_health() {
		{
			HttpClientHelperResult<String> result = httpClientHelper.doGet(HttpClientHelper.createURI(() -> {
				URIBuilder builder = new URIBuilder(baseUrl + "/actuator/health");
				return builder.build();
			}));

			LOG.trace("the response: {}", result.getBodyAsString());
			Assertions.assertEquals(HttpStatus.OK, HttpStatus.valueOf(result.getStatusLine().getCode()));
			JsonNode rspObj = McckJsonUtil.toJsonNode(result.getBodyAsString());
			Assertions.assertEquals("UP", rspObj.get("status").asText());
		}
	}

	@Test
	void test_health_liveness() {
		{
			HttpClientHelperResult<String> result = httpClientHelper.doGet(HttpClientHelper.createURI(() -> {
				URIBuilder builder = new URIBuilder(baseUrl + "/actuator/health/liveness");
				return builder.build();
			}));

			LOG.trace("the response: {}", result.getBodyAsString());
			Assertions.assertEquals(HttpStatus.OK, HttpStatus.valueOf(result.getStatusLine().getCode()));
			JsonNode rspObj = McckJsonUtil.toJsonNode(result.getBodyAsString());
			Assertions.assertEquals("UP", rspObj.get("status").asText());
		}
	}

	@Test
	void test_health_readiness() {
		{
			HttpClientHelperResult<String> result = httpClientHelper.doGet(HttpClientHelper.createURI(() -> {
				URIBuilder builder = new URIBuilder(baseUrl + "/actuator/health/readiness");
				return builder.build();
			}));

			LOG.trace("the response: {}", result.getBodyAsString());
			Assertions.assertEquals(HttpStatus.OK, HttpStatus.valueOf(result.getStatusLine().getCode()));
			JsonNode rspObj = McckJsonUtil.toJsonNode(result.getBodyAsString());
			Assertions.assertEquals("UP", rspObj.get("status").asText());
		}
	}

	@Test
	void test_info() {
		{
			HttpClientHelperResult<String> result = httpClientHelper.doGet(HttpClientHelper.createURI(() -> {
				URIBuilder builder = new URIBuilder(baseUrl + "/actuator/info");
				return builder.build();
			}));

			LOG.trace("the response: {}", result.getBodyAsString());
			Assertions.assertEquals(HttpStatus.OK, HttpStatus.valueOf(result.getStatusLine().getCode()));
			JsonNode rspObj = McckJsonUtil.toJsonNode(result.getBodyAsString());
			JsonNode jn = McckJsonUtil.extractField(rspObj, "build", "name");
			Assertions.assertEquals("mcck-core-EXAMPLES-springboot-rest-hibernate", jn.asText());
		}
	}

	@Test
	void test_metrics() {
		String[] metricName = { null };
		{
			HttpClientHelperResult<String> result = httpClientHelper.doGet(HttpClientHelper.createURI(() -> {
				URIBuilder builder = new URIBuilder(baseUrl + "/actuator/metrics");
				return builder.build();
			}));

			LOG.trace("the response: {}", result.getBodyAsString());
			Assertions.assertEquals(HttpStatus.OK, HttpStatus.valueOf(result.getStatusLine().getCode()));
			JsonNode rspObj = McckJsonUtil.toJsonNode(result.getBodyAsString());
			JsonNode jn = McckJsonUtil.extractField(rspObj, "names");
			Assertions.assertEquals(false, McckJsonUtil.isNull(jn));
			ArrayNode an = (ArrayNode) jn;
			Assertions.assertEquals(false, an.isEmpty());
			metricName[0] = an.get(0).asText();
		}
		// retrieve information about the specific metric
		{
			HttpClientHelperResult<String> result = httpClientHelper.doGet(HttpClientHelper.createURI(() -> {
				URIBuilder builder = new URIBuilder(baseUrl + "/actuator/metrics/" + metricName[0]);
				return builder.build();
			}));

			LOG.trace("the response: {}", result.getBodyAsString());
			Assertions.assertEquals(HttpStatus.OK, HttpStatus.valueOf(result.getStatusLine().getCode()));
			JsonNode rspObj = McckJsonUtil.toJsonNode(result.getBodyAsString());
			JsonNode jn = McckJsonUtil.extractField(rspObj, "name");
			Assertions.assertEquals(false, McckJsonUtil.isNull(jn));
			Assertions.assertEquals(false, jn.asText().isEmpty());
		}
	}

	@Test
	void test_prometheus() {
		{
			HttpClientHelperResult<String> result = httpClientHelper.doGet(HttpClientHelper.createURI(() -> {
				URIBuilder builder = new URIBuilder(baseUrl + "/actuator/prometheus");
				return builder.build();
			}), hdr -> {
				// Prometheus output is in plaintext, so ensure to set an Accept
				// header value accordingly otherwise an error will be returned
				// by the endpoint
				hdr.setHeader(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN_VALUE);
			});

			LOG.trace("the response: {}", result.getBodyAsString());
			Assertions.assertEquals(HttpStatus.OK, HttpStatus.valueOf(result.getStatusLine().getCode()));
			Assertions.assertEquals(true, result.getBodyAsString().contains("system_cpu_usage"));
		}
	}
}
