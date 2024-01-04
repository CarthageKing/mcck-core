package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.controller;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.annotation.Resource;

@ContextConfiguration(classes = { TestSpringConfig.class, CommonConfig.class, TestDbConfig.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
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
}
