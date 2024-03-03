package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.cucumber_tests;

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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperResult;
import org.carthageking.mc.mcck.core.json.McckJsonUtil;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.Resource;

public class StepDefinitions {

	public static final String HTTP_RESP_OBJ = "HTTP_RESP_OBJ";
	public static final String HTTP_RESP_JSON = "HTTP_RESP_JSON";

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StepDefinitions.class);

	@LocalServerPort
	private int port;

	@Resource
	private HttpClientHelper httpClientHelper;

	private String baseUrl;

	private static ThreadLocal<Map<String, Object>> scenarioCtx = ThreadLocal.withInitial(() -> new HashMap<>());

	@Before
	public void beforeBeginScenario() {
		scenarioCtx.get().clear();
		baseUrl = "http://localhost:" + port;
		LOG.trace("Initializing Cucumber scenario context");
	}

	@After
	public void afterEndScenario() {
		scenarioCtx.get().clear();
		scenarioCtx.remove();
		LOG.trace("Destroying Cucumber scenario context");
	}

	@When("I call the GET API {string}")
	public void whenICallTheGetApiContextPath(String contextPath) {
		HttpClientHelperResult<String> result = httpClientHelper.doGet(HttpClientHelper.createURI(() -> {
			URIBuilder builder = new URIBuilder(baseUrl + contextPath);
			URI uri = builder.build();
			LOG.trace("calling HTTP GET API: {}", uri);
			return uri;
		}));
		sceCtxSet(HTTP_RESP_OBJ, result);
	}

	@Then("The response must be {string}")
	public void thenTheResponseMustBeResponseDataType(String responseDataTypeStr) {
		assertThatScenarioContextVariablesSet(HTTP_RESP_OBJ);

		ResponseDataType responseDataType = ResponseDataType.valueOf(responseDataTypeStr);
		switch (responseDataType) {
		case JSON:
			HttpClientHelperResult<String> rspObj = sceCtxGet(HTTP_RESP_OBJ);
			LOG.trace("the HTTP response: {} {}", rspObj.getStatusLine().getCode(), rspObj.getBodyAsString());
			JsonNode jn = McckJsonUtil.toJsonNode(rspObj.getBodyAsString());
			sceCtxSet(HTTP_RESP_JSON, jn);
			break;

		default:
			throw new IllegalArgumentException("Unsupported response data type " + responseDataType);
		}
	}

	@Then("The JSON response must contain the following:")
	public void thenTheJsonResponseMustContainTheFollowing(DataTable tbl) {
		assertThatScenarioContextVariablesSet(HTTP_RESP_JSON);

		JsonNode jn = sceCtxGet(HTTP_RESP_JSON);
		List<String> errors = new ArrayList<>();

		// start at row 1. row 0 will contain our column names
		for (int i = 1; i < tbl.cells().size(); i++) {
			String path = dataTblGet(tbl, "Path", i);
			String val = dataTblGet(tbl, "Value", i);
			JsonNode innerN = McckJsonUtil.extractFieldAtPath(jn, path);

			if (McckJsonUtil.isNull(innerN)) {
				errors.add("Null value found at JSON path: " + path);
			} else {
				if (!val.equals(innerN.asText())) {
					errors.add("For path '" + path + "', expected [" + val + "] but was [" + innerN.asText() + "]");
				}
			}
		}

		if (!errors.isEmpty()) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.println("Errors found while validating JSON response:");
			pw.println();
			for (String err : errors) {
				pw.println(err);
			}
			Assertions.fail(sw.toString());
		}
	}

	@Then("The response status must be {int}")
	public void thenTheResponseStatusMustBeStatusCode(int statusCode) {
		assertThatScenarioContextVariablesSet(HTTP_RESP_OBJ);

		HttpClientHelperResult<String> rspObj = sceCtxGet(HTTP_RESP_OBJ);
		Assertions.assertEquals(statusCode, rspObj.getStatusLine().getCode());
	}

	@SuppressWarnings("unchecked")
	private <T> T sceCtxGet(String key) {
		return (T) scenarioCtx.get().get(key);
	}

	private void sceCtxSet(String key, Object obj) {
		scenarioCtx.get().put(key, obj);
	}

	private String dataTblGet(DataTable tbl, String colName, int rowIdx) {
		List<String> hdrs = tbl.row(0);
		for (int i = 0; i < hdrs.size(); i++) {
			if (hdrs.get(i).equals(colName)) {
				return tbl.cell(rowIdx, i);
			}
		}
		throw new IllegalArgumentException("Cannot find the column index for column name: " + colName);
	}

	private void assertThatScenarioContextVariablesSet(String... keys) {
		List<String> missing = new ArrayList<>();
		for (String k : keys) {
			if (null == sceCtxGet(k)) {
				missing.add(k);
			}
		}
		if (missing.size() > 0) {
			throw new IllegalStateException("The following variables are missing/not set: " + missing);
		}
	}
}
