package org.carthageking.mc.mcck.core.httpclient.okhttp3;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpHeaders;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperException;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperResult;
import org.carthageking.mc.mcck.core.httpclient.HttpMimeTypes;
import org.carthageking.mc.mcck.core.httpclient.StatusLine;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttp3HttpClientHelper implements HttpClientHelper {

	private final OkHttpClient httpClient;

	public OkHttp3HttpClientHelper(OkHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public OkHttpClient getHttpClient() {
		return httpClient;
	}

	@Override
	public HttpClientHelperResult<String> doGet(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper) {
		Request.Builder httpReqBuilder = new Request.Builder()
			.get()
			.url(HttpUrl.get(requestUri));
		if (null != httpHdrModHelper) {
			httpHdrModHelper.modify(createHttpHeaderModifier(httpReqBuilder));
		} else {
			httpReqBuilder.header(HttpHeaders.ACCEPT, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
		}
		return doRequestResponse(httpReqBuilder.build());
	}

	@Override
	public HttpClientHelperResult<String> doPost(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		Request.Builder httpReqBuilder = new Request.Builder()
			.post(RequestBody.create(body.getBytes(StandardCharsets.UTF_8)))
			.url(HttpUrl.get(requestUri));
		if (null != httpHdrModHelper) {
			httpHdrModHelper.modify(createHttpHeaderModifier(httpReqBuilder));
		} else {
			httpReqBuilder.header(HttpHeaders.CONTENT_TYPE, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
			httpReqBuilder.header(HttpHeaders.ACCEPT, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
		}
		return doRequestResponse(httpReqBuilder.build());
	}

	private HttpClientHelperResult<String> doRequestResponse(Request httpReq) {
		try (Response httpRsp = httpClient.newCall(httpReq).execute()) {
			try (ResponseBody rbody = httpRsp.body()) {
				StatusLine status = new StatusLine(httpRsp.code(), httpRsp.message());
				return new HttpClientHelperResult<>(status, httpRsp.headers().toMultimap(), rbody.string());
			}
		} catch (IOException e) {
			throw new HttpClientHelperException("An error occurred", e);
		}
	}

	private HttpHeadersModifier createHttpHeaderModifier(Request.Builder builder) {
		return new HttpHeadersModifier() {

			@Override
			public void setHeader(String name, String value) {
				builder.header(name, value);
			}

			@Override
			public void addHeader(String name, String value) {
				builder.addHeader(name, value);
			}
		};
	}
}
