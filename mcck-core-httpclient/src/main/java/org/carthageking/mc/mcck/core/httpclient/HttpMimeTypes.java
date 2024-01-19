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

import java.nio.charset.StandardCharsets;

import org.apache.http.entity.ContentType;

public enum HttpMimeTypes {

	APPLICATION_XML(ContentType.APPLICATION_XML.getMimeType()),
	APPLICATION_XML_UTF8(ContentType.APPLICATION_XML.withCharset(StandardCharsets.UTF_8).getMimeType()),
	APPLICATION_JSON(ContentType.APPLICATION_JSON.getMimeType()),
	APPLICATION_JSON_UTF8(ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8).getMimeType()),
	APPLICATION_FORM_URLENCODED(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());

	private final String mimeTypeAsString;

	HttpMimeTypes(String mimeTypeAsString) {
		this.mimeTypeAsString = mimeTypeAsString;
	}

	public String getMimeTypeAsString() {
		return mimeTypeAsString;
	}
}
