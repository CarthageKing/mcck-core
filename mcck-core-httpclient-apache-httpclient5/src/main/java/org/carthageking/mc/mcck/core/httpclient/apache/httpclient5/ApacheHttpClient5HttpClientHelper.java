package org.carthageking.mc.mcck.core.httpclient.apache.httpclient5;

/*-
 * #%L
 * mcck-core-httpclient-apache-httpclient5
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

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperException;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperResult;
import org.carthageking.mc.mcck.core.httpclient.HttpMimeTypes;
import org.carthageking.mc.mcck.core.httpclient.StatusLine;

public class ApacheHttpClient5HttpClientHelper implements HttpClientHelper {

	private final HttpClient httpClient;

	public ApacheHttpClient5HttpClientHelper(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	@Override
	public HttpClientHelperResult<String> doGet(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper) {
		HttpGet httpReq = new HttpGet(requestUri);
		return doRequestNoBody(httpHdrModHelper, httpReq);
	}

	@Override
	public HttpClientHelperResult<String> doDelete(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper) {
		HttpDelete httpReq = new HttpDelete(requestUri);
		return doRequestNoBody(httpHdrModHelper, httpReq);
	}

	private HttpClientHelperResult<String> doRequestNoBody(HttpHeadersModifierHelper httpHdrModHelper, ClassicHttpRequest httpReq) {
		if (null != httpHdrModHelper) {
			httpHdrModHelper.modify(createHttpHeaderModifier(httpReq));
		} else {
			httpReq.setHeader(HttpHeaders.ACCEPT, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
		}
		return doRequestResponse(httpReq);
	}

	@Override
	public HttpClientHelperResult<String> doPost(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		HttpPost httpReq = new HttpPost(requestUri);
		return doRequestWithBody(httpHdrModHelper, httpReq, body);
	}

	@Override
	public HttpClientHelperResult<String> doPut(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		HttpPut httpReq = new HttpPut(requestUri);
		return doRequestWithBody(httpHdrModHelper, httpReq, body);
	}

	@Override
	public HttpClientHelperResult<String> doPatch(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		HttpPatch httpReq = new HttpPatch(requestUri);
		return doRequestWithBody(httpHdrModHelper, httpReq, body);
	}

	private HttpEntity createStringRequestBody(String body) {
		return new StringEntity(body, StandardCharsets.UTF_8);
	}

	private HttpClientHelperResult<String> doRequestWithBody(HttpHeadersModifierHelper httpHdrModHelper, ClassicHttpRequest httpReq, String body) {
		if (null != httpHdrModHelper) {
			httpHdrModHelper.modify(createHttpHeaderModifier(httpReq));
		} else {
			httpReq.setHeader(HttpHeaders.CONTENT_TYPE, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
			httpReq.setHeader(HttpHeaders.ACCEPT, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
		}
		httpReq.setEntity(createStringRequestBody(body));
		return doRequestResponse(httpReq);
	}

	private HttpClientHelperResult<String> doRequestResponse(ClassicHttpRequest httpReq) {
		try {
			return httpClient.execute(httpReq, rsp -> {
				StatusLine status = new StatusLine(rsp.getCode(), rsp.getReasonPhrase());
				HttpEntity rspEntity = rsp.getEntity();
				String body = "";
				if (null != rspEntity) {
					body = EntityUtils.toString(rspEntity, StandardCharsets.UTF_8);
				}
				return new HttpClientHelperResult<>(status, toMap(rsp.headerIterator()), body);
			});
		} catch (IOException e) {
			throw new HttpClientHelperException(e);
		}
	}

	private Map<String, List<String>> toMap(Iterator<Header> headerIterator) {
		Map<String, List<String>> map = new HashMap<>();
		while (headerIterator.hasNext()) {
			Header hdr = headerIterator.next();
			List<String> lst = map.computeIfAbsent(hdr.getName(), n -> new ArrayList<>());
			lst.add(hdr.getValue());
		}
		return map;
	}

	private HttpHeadersModifier createHttpHeaderModifier(ClassicHttpRequest httpReq) {
		return new HttpHeadersModifier() {

			@Override
			public void setHeader(String name, String value) {
				httpReq.setHeader(name, value);
			}

			@Override
			public void addHeader(String name, String value) {
				httpReq.addHeader(name, value);
			}
		};
	}
}
