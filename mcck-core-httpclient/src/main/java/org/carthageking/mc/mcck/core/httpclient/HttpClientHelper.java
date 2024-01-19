package org.carthageking.mc.mcck.core.httpclient;

/*-
 * #%L
 * mcck-core-httpclient
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.http.NameValuePair;

public interface HttpClientHelper {

	default HttpClientHelperResult<String> doGet(URI requestUri) {
		return doGet(requestUri, null);
	}

	HttpClientHelperResult<String> doGet(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper);

	default HttpClientHelperResult<String> doPost(URI requestUri, String body) {
		return doPost(requestUri, body, null);
	}

	HttpClientHelperResult<String> doPost(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper);

	default HttpClientHelperResult<String> doPut(URI requestUri, String body) {
		return doPut(requestUri, body, null);
	}

	HttpClientHelperResult<String> doPut(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper);

	default HttpClientHelperResult<String> doPatch(URI requestUri, String body) {
		return doPatch(requestUri, body, null);
	}

	HttpClientHelperResult<String> doPatch(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper);

	default HttpClientHelperResult<String> doDelete(URI requestUri) {
		return doDelete(requestUri, null);
	}

	HttpClientHelperResult<String> doDelete(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper);

	static URI createURI(HttpUriCreator uriCreator) {
		try {
			return uriCreator.buildUri();
		} catch (Exception e) {
			throw new HttpClientHelperException(e);
		}
	}

	static String createUrlEncoded(List<? extends NameValuePair> pairs) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (NameValuePair nvp : pairs) {
			if (!first) {
				sb.append("&");
			}
			first = false;
			sb.append(nvp.getName()).append("=").append(URLEncoder.encode(nvp.getValue(), StandardCharsets.UTF_8));
		}
		return sb.toString();
	}

	@FunctionalInterface
	interface HttpUriCreator {
		URI buildUri() throws Exception;
	}

	@FunctionalInterface
	interface HttpHeadersModifierHelper {
		void modify(HttpHeadersModifier modifier);
	}

	interface HttpHeadersModifier {

		void addHeader(String name, String value);

		void setHeader(String name, String value);
	}
}
