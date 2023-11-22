package org.carthageking.mc.mcck.core.httpclient;

import java.net.URI;

public interface HttpClientHelper {

	default HttpClientHelperResult<String> doGet(URI requestUri) {
		return doGet(requestUri, null);
	}

	HttpClientHelperResult<String> doGet(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper);

	default HttpClientHelperResult<String> doPost(URI requestUri, String body) {
		return doPost(requestUri, body, null);
	}

	HttpClientHelperResult<String> doPost(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper);

	static URI createURI(HttpUriCreator uriCreator) {
		try {
			return uriCreator.buildUri();
		} catch (Exception e) {
			throw new HttpClientHelperException("An error occurred while building URI", e);
		}
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
