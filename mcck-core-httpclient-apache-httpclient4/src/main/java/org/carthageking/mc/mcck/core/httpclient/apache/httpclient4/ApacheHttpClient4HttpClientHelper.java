package org.carthageking.mc.mcck.core.httpclient.apache.httpclient4;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperException;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperResult;
import org.carthageking.mc.mcck.core.httpclient.HttpMimeTypes;
import org.carthageking.mc.mcck.core.httpclient.StatusLine;

public class ApacheHttpClient4HttpClientHelper implements HttpClientHelper {

	private final HttpClient httpClient;

	public ApacheHttpClient4HttpClientHelper(HttpClient httpClient) {
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

	private HttpClientHelperResult<String> doRequestNoBody(HttpHeadersModifierHelper httpHdrModHelper, HttpRequestBase httpReq) {
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

	private HttpClientHelperResult<String> doRequestWithBody(HttpHeadersModifierHelper httpHdrModHelper, HttpEntityEnclosingRequestBase httpReq, String body) {
		if (null != httpHdrModHelper) {
			httpHdrModHelper.modify(createHttpHeaderModifier(httpReq));
		} else {
			httpReq.setHeader(HttpHeaders.CONTENT_TYPE, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
			httpReq.setHeader(HttpHeaders.ACCEPT, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
		}
		httpReq.setEntity(createStringRequestBody(body));
		return doRequestResponse(httpReq);
	}

	private HttpClientHelperResult<String> doRequestResponse(HttpRequestBase httpReq) {
		try (CloseableHttpResponse httpRsp = (CloseableHttpResponse) httpClient.execute(httpReq)) {
			StatusLine status = new StatusLine(httpRsp.getStatusLine().getStatusCode(), httpRsp.getStatusLine().getReasonPhrase());
			HttpEntity httpRspEntity = httpRsp.getEntity();
			String body = "";
			if (null != httpRspEntity) {
				body = EntityUtils.toString(httpRspEntity, StandardCharsets.UTF_8);
			}
			return new HttpClientHelperResult<>(status, toMap(httpRsp.headerIterator()), body);
		} catch (IOException e) {
			throw new HttpClientHelperException(e);
		}
	}

	private Map<String, List<String>> toMap(HeaderIterator headerIterator) {
		Map<String, List<String>> map = new HashMap<>();
		while (headerIterator.hasNext()) {
			Header hdr = headerIterator.nextHeader();
			List<String> lst = map.computeIfAbsent(hdr.getName(), n -> new ArrayList<>());
			lst.add(hdr.getValue());
		}
		return map;
	}

	private HttpHeadersModifier createHttpHeaderModifier(HttpRequestBase httpReq) {
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
