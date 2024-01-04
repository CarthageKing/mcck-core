package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model;

/*-
 * #%L
 * mcck-core-EXAMPLES-springboot-rest-hibernate
 * %%
 * Copyright (C) 2024 Michael I. Calderero
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class GenericResponse<T extends java.io.Serializable> implements java.io.Serializable {

	private static final long serialVersionUID = 2198624286618367019L;

	private GenericResponseHeader header;
	private T data;

	public GenericResponse() {
		// noop
	}

	public GenericResponseHeader getHeader() {
		return header;
	}

	public void setHeader(GenericResponseHeader header) {
		this.header = header;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@JsonInclude(Include.NON_EMPTY)
	public static class GenericResponseHeader implements java.io.Serializable {

		private static final long serialVersionUID = 6035493325976244203L;

		private String statusCode;
		private String statusMessage;

		public GenericResponseHeader() {
			// noop
		}

		public String getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(String statusCode) {
			this.statusCode = statusCode;
		}

		public String getStatusMessage() {
			return statusMessage;
		}

		public void setStatusMessage(String statusMessage) {
			this.statusMessage = statusMessage;
		}
	}
}
