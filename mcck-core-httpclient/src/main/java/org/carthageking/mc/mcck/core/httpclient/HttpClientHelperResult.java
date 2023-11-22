package org.carthageking.mc.mcck.core.httpclient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpClientHelperResult<T> {

	private final StatusLine statusLine;
	private final T body;
	private final Map<String, List<String>> headers;

	public HttpClientHelperResult(StatusLine statusLine, Map<String, List<String>> headers, T body) {
		this.statusLine = statusLine;
		this.body = body;
		this.headers = headers;
	}

	public StatusLine getStatusLine() {
		return statusLine;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public Optional<T> getBody() {
		return (null == body ? Optional.empty() : Optional.of(body));
	}

	public String getBodyAsString() {
		return (null == body ? null : body.toString());
	}
}
