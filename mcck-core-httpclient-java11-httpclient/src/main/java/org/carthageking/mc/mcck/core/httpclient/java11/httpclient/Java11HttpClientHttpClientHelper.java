package org.carthageking.mc.mcck.core.httpclient.java11.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpHeaders;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperException;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperResult;
import org.carthageking.mc.mcck.core.httpclient.HttpMimeTypes;
import org.carthageking.mc.mcck.core.httpclient.StatusLine;

public class Java11HttpClientHttpClientHelper implements HttpClientHelper {

	private final HttpClient httpClient;

	public Java11HttpClientHttpClientHelper(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	@Override
	public HttpClientHelperResult<String> doGet(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper) {
		HttpRequest.Builder httpReqBuilder = HttpRequest.newBuilder(requestUri).GET();
		return doRequestNoBody(httpHdrModHelper, httpReqBuilder);
	}

	@Override
	public HttpClientHelperResult<String> doDelete(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper) {
		HttpRequest.Builder httpReqBuilder = HttpRequest.newBuilder(requestUri).DELETE();
		return doRequestNoBody(httpHdrModHelper, httpReqBuilder);
	}

	private HttpClientHelperResult<String> doRequestNoBody(HttpHeadersModifierHelper httpHdrModHelper, Builder httpReqBuilder) {
		if (null != httpHdrModHelper) {
			httpHdrModHelper.modify(createHttpHeaderModifier(httpReqBuilder));
		} else {
			httpReqBuilder.header(HttpHeaders.ACCEPT, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
		}
		return doRequestResponse(httpReqBuilder.build());
	}

	@Override
	public HttpClientHelperResult<String> doPost(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		HttpRequest.Builder httpReqBuilder = HttpRequest.newBuilder(requestUri)
			.POST(createStringRequestBody(body));
		return doRequestWithBody(httpHdrModHelper, httpReqBuilder);
	}

	@Override
	public HttpClientHelperResult<String> doPut(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		HttpRequest.Builder httpReqBuilder = HttpRequest.newBuilder(requestUri)
			.PUT(createStringRequestBody(body));
		return doRequestWithBody(httpHdrModHelper, httpReqBuilder);
	}

	@Override
	public HttpClientHelperResult<String> doPatch(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		HttpRequest.Builder httpReqBuilder = HttpRequest.newBuilder(requestUri)
			.method("PATCH", createStringRequestBody(body));
		return doRequestWithBody(httpHdrModHelper, httpReqBuilder);
	}

	private BodyPublisher createStringRequestBody(String body) {
		return BodyPublishers.ofString(body, StandardCharsets.UTF_8);
	}

	private HttpClientHelperResult<String> doRequestWithBody(HttpHeadersModifierHelper httpHdrModHelper, Builder httpReqBuilder) {
		if (null != httpHdrModHelper) {
			httpHdrModHelper.modify(createHttpHeaderModifier(httpReqBuilder));
		} else {
			httpReqBuilder.header(HttpHeaders.CONTENT_TYPE, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
			httpReqBuilder.header(HttpHeaders.ACCEPT, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
		}
		return doRequestResponse(httpReqBuilder.build());
	}

	private HttpClientHelperResult<String> doRequestResponse(HttpRequest httpReq) {
		try {
			HttpResponse<String> httpRsp = httpClient.send(httpReq, BodyHandlers.ofString(StandardCharsets.UTF_8));
			StatusLine status = new StatusLine(httpRsp.statusCode(), null);
			return new HttpClientHelperResult<>(status, httpRsp.headers().map(), httpRsp.body());
		} catch (IOException | InterruptedException e) {
			throw new HttpClientHelperException(e);
		}
	}

	private HttpHeadersModifier createHttpHeaderModifier(HttpRequest.Builder builder) {
		return new HttpHeadersModifier() {

			@Override
			public void setHeader(String name, String value) {
				builder.setHeader(name, value);
			}

			@Override
			public void addHeader(String name, String value) {
				builder.header(name, value);
			}
		};
	}
}
