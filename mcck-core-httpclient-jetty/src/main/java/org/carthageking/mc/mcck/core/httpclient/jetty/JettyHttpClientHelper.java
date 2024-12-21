package org.carthageking.mc.mcck.core.httpclient.jetty;

/*-
 * #%L
 * mcck-core-httpclient-jetty
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

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpHeaders;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperException;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperResult;
import org.carthageking.mc.mcck.core.httpclient.HttpMimeTypes;
import org.carthageking.mc.mcck.core.httpclient.StatusLine;
import org.eclipse.jetty.client.ContentResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Request.Content;
import org.eclipse.jetty.client.StringRequestContent;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpMethod;

/**
 * <p>
 * Jetty HttpClient needs to be started before using it here. This class does not handle
 * the starting of the Jetty HttpClient.
 * </p>
 */
public class JettyHttpClientHelper implements HttpClientHelper {

	private final HttpClient httpClient;

	public JettyHttpClientHelper(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	@Override
	public HttpClientHelperResult<String> doGet(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper) {
		Request httpReq = httpClient.newRequest(requestUri);
		httpReq = httpReq.method(HttpMethod.GET);
		return doRequestNoBody(httpHdrModHelper, httpReq);
	}

	@Override
	public HttpClientHelperResult<String> doDelete(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper) {
		Request httpReq = httpClient.newRequest(requestUri);
		httpReq = httpReq.method(HttpMethod.DELETE);
		return doRequestNoBody(httpHdrModHelper, httpReq);
	}

	private HttpClientHelperResult<String> doRequestNoBody(HttpHeadersModifierHelper httpHdrModHelper, Request httpReq) {
		Map<String, List<String>> headers = new HashMap<>();
		HttpHeadersModifier hdrMod = createHttpHeaderModifier(headers);
		if (null != httpHdrModHelper) {
			httpHdrModHelper.modify(hdrMod);
		} else {
			hdrMod.setHeader(HttpHeaders.ACCEPT, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
		}
		return doRequestResponse(httpReq, headers);
	}

	@Override
	public HttpClientHelperResult<String> doPost(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		Request httpReq = httpClient.newRequest(requestUri);
		httpReq = httpReq.method(HttpMethod.POST);
		httpReq = httpReq.body(createStringRequestBody(body));
		return doRequestWithBody(httpHdrModHelper, httpReq);
	}

	@Override
	public HttpClientHelperResult<String> doPut(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		Request httpReq = httpClient.newRequest(requestUri);
		httpReq = httpReq.method(HttpMethod.PUT);
		httpReq = httpReq.body(createStringRequestBody(body));
		return doRequestWithBody(httpHdrModHelper, httpReq);
	}

	@Override
	public HttpClientHelperResult<String> doPatch(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		Request httpReq = httpClient.newRequest(requestUri);
		httpReq = httpReq.method(HttpMethod.PATCH);
		httpReq = httpReq.body(createStringRequestBody(body));
		return doRequestWithBody(httpHdrModHelper, httpReq);
	}

	private Content createStringRequestBody(String body) {
		return new StringRequestContent(body, StandardCharsets.UTF_8);
	}

	private HttpClientHelperResult<String> doRequestWithBody(HttpHeadersModifierHelper httpHdrModHelper, Request httpReq) {
		Map<String, List<String>> headers = new HashMap<>();
		HttpHeadersModifier hdrMod = createHttpHeaderModifier(headers);
		if (null != httpHdrModHelper) {
			httpHdrModHelper.modify(hdrMod);
		} else {
			hdrMod.setHeader(HttpHeaders.CONTENT_TYPE, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
			hdrMod.setHeader(HttpHeaders.ACCEPT, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
		}
		return doRequestResponse(httpReq, headers);
	}

	private HttpClientHelperResult<String> doRequestResponse(Request httpReq, Map<String, List<String>> headers) {
		httpReq.headers(hdr -> {
			hdr.clear();
			for (Entry<String, List<String>> ent : headers.entrySet()) {
				for (String v : ent.getValue()) {
					hdr.add(ent.getKey(), v);
				}
			}
		});
		try {
			ContentResponse httpRsp = httpReq.send();
			StatusLine status = new StatusLine(httpRsp.getStatus(), null);
			String responseBody = httpRsp.getContentAsString();
			return new HttpClientHelperResult<>(status, toMap(httpRsp.getHeaders()), responseBody);
		} catch (Exception e) {
			throw new HttpClientHelperException(e);
		}
	}

	private Map<String, List<String>> toMap(HttpFields headers) {
		Map<String, List<String>> map = new HashMap<>();
		for (HttpField hf : headers) {
			String key = hf.getName();
			List<String> lst = map.computeIfAbsent(key, k -> new ArrayList<>());
			Collections.addAll(lst, hf.getValues());
		}
		return map;
	}

	private HttpHeadersModifier createHttpHeaderModifier(Map<String, List<String>> headers) {
		return new HttpHeadersModifier() {

			@Override
			public void setHeader(String name, String value) {
				List<String> lst = headers.computeIfAbsent(name, n -> new ArrayList<>());
				lst.clear();
				lst.add(value);
			}

			@Override
			public void addHeader(String name, String value) {
				List<String> lst = headers.computeIfAbsent(name, n -> new ArrayList<>());
				lst.add(value);
			}
		};
	}
}
