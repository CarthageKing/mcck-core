package org.carthageking.mc.mcck.core.httpclient.jse.urlconnection;

/*-
 * #%L
 * mcck-core-httpclient-jse-urlconnection
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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpHeaders;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperException;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperResult;
import org.carthageking.mc.mcck.core.httpclient.HttpMimeTypes;
import org.carthageking.mc.mcck.core.httpclient.StatusLine;
import org.carthageking.mc.mcck.core.jse.McckIOUtil;

/**
 * <p>
 * This class does not support PATCH method as per https://bugs.openjdk.org/browse/JDK-7016595.
 * There is a workaround which involves reflection as per https://stackoverflow.com/a/46323891
 * but that will have to be coded by the user of this library.
 * </p> 
 * <p>
 * There is also another workaround but it requires the receiving server to recognize the
 * additional request headers: https://stackoverflow.com/a/32503192
 * </p>
 */
public class JseUrlConnectionHttpClientHelper implements HttpClientHelper {

	public JseUrlConnectionHttpClientHelper() {
		// noop
	}

	@Override
	public HttpClientHelperResult<String> doGet(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper) {
		return doRequestNoBody(requestUri, httpHdrModHelper, "GET");
	}

	@Override
	public HttpClientHelperResult<String> doDelete(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper) {
		return doRequestNoBody(requestUri, httpHdrModHelper, "DELETE");
	}

	private HttpClientHelperResult<String> doRequestNoBody(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper, String method) {
		try {
			URL requestUrl = requestUri.toURL();
			HttpURLConnection httpReq = (HttpURLConnection) requestUrl.openConnection();
			httpReq.setRequestMethod(method);

			if (null != httpHdrModHelper) {
				httpHdrModHelper.modify(createHttpHeaderModifier(httpReq));
			} else {
				httpReq.setRequestProperty(HttpHeaders.ACCEPT, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
			}

			try {
				httpReq.connect();
			} catch (IOException e) {
				throw new HttpClientHelperException(e);
			}

			return readResponseBodyAsUtf8String(httpReq);
		} catch (IOException e) {
			throw new HttpClientHelperException(e);
		}
	}

	private HttpClientHelperResult<String> readResponseBodyAsUtf8String(HttpURLConnection httpReq) {
		try (InputStream is = httpReq.getInputStream()) {
			StatusLine status = new StatusLine(httpReq.getResponseCode(), httpReq.getResponseMessage());
			String responseBody = McckIOUtil.readAllAsString(is, StandardCharsets.UTF_8);
			return new HttpClientHelperResult<>(status, httpReq.getHeaderFields(), responseBody);
		} catch (IOException e) {
			throw new HttpClientHelperException(e);
		}
	}

	@Override
	public HttpClientHelperResult<String> doPost(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		return doRequestResponse(requestUri, body, httpHdrModHelper, "POST");
	}

	@Override
	public HttpClientHelperResult<String> doPut(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		return doRequestResponse(requestUri, body, httpHdrModHelper, "PUT");
	}

	@Override
	public HttpClientHelperResult<String> doPatch(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		return doRequestResponse(requestUri, body, httpHdrModHelper, "PATCH");
	}

	private HttpClientHelperResult<String> doRequestResponse(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper, String method) {
		try {
			URL requestUrl = requestUri.toURL();
			HttpURLConnection httpReq = (HttpURLConnection) requestUrl.openConnection();
			httpReq.setRequestMethod(method);
			httpReq.setDoOutput(true);

			if (null != httpHdrModHelper) {
				httpHdrModHelper.modify(createHttpHeaderModifier(httpReq));
			} else {
				httpReq.setRequestProperty(HttpHeaders.CONTENT_TYPE, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
				httpReq.setRequestProperty(HttpHeaders.ACCEPT, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
			}

			try {
				httpReq.connect();
			} catch (IOException e) {
				throw new HttpClientHelperException(e);
			}

			try (OutputStream os = httpReq.getOutputStream()) {
				os.write(body.getBytes(StandardCharsets.UTF_8));
				os.flush();
			} catch (IOException e) {
				throw new HttpClientHelperException(e);
			}

			return readResponseBodyAsUtf8String(httpReq);
		} catch (IOException e) {
			throw new HttpClientHelperException(e);
		}
	}

	private HttpHeadersModifier createHttpHeaderModifier(HttpURLConnection conn) {
		return new HttpHeadersModifier() {

			@Override
			public void setHeader(String name, String value) {
				conn.setRequestProperty(name, value);
			}

			@Override
			public void addHeader(String name, String value) {
				conn.addRequestProperty(name, value);
			}
		};
	}
}
