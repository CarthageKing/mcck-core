package org.carthageking.mc.mcck.core.httpclient;

import java.nio.charset.StandardCharsets;

import org.apache.http.entity.ContentType;

public enum HttpMimeTypes {

	APPLICATION_XML(ContentType.APPLICATION_XML.getMimeType()),
	APPLICATION_XML_UTF8(ContentType.APPLICATION_XML.withCharset(StandardCharsets.UTF_8).getMimeType()),
	APPLICATION_JSON(ContentType.APPLICATION_JSON.getMimeType()),
	APPLICATION_JSON_UTF8(ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8).getMimeType());

	private final String mimeTypeAsString;

	private HttpMimeTypes(String mimeTypeAsString) {
		this.mimeTypeAsString = mimeTypeAsString;
	}

	public String getMimeTypeAsString() {
		return mimeTypeAsString;
	}
}
