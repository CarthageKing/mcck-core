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
