package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.exception;

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

import java.io.Serializable;

import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.GenericResponse;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.GenericResponse.GenericResponseHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppCustomExceptionHandler extends ResponseEntityExceptionHandler {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AppCustomExceptionHandler.class);

	public AppCustomExceptionHandler() {
		// noop
	}

	@ExceptionHandler({ AppCustomException.class })
	public ResponseEntity<GenericResponse<Serializable>> handleAppCustomException(Exception e, WebRequest request) {
		GenericResponse<Serializable> rsp = new GenericResponse<>();
		GenericResponseHeader hdr = new GenericResponseHeader();
		rsp.setHeader(hdr);
		if (e instanceof AppCustomResultNotFoundException) {
			hdr.setStatusCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
			hdr.setStatusMessage(e.getMessage());
		} else {
			// for generic exceptions, treat everything as sensitive info and do
			// not leak to the response object
			hdr.setStatusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
			hdr.setStatusMessage("An internal error occurred");
		}
		LOG.error("An error occurred", e);
		return ResponseEntity.status(HttpStatus.valueOf(Integer.valueOf(hdr.getStatusCode()))).body(rsp);
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<GenericResponse<Serializable>> handleGenericException(Exception e, WebRequest request) {
		GenericResponse<Serializable> rsp = new GenericResponse<>();
		GenericResponseHeader hdr = new GenericResponseHeader();
		rsp.setHeader(hdr);
		// for generic exceptions, treat everything as sensitive info and do
		// not leak to the response object
		hdr.setStatusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		hdr.setStatusMessage("An internal error occurred");
		LOG.error("An error occurred", e);
		return ResponseEntity.status(HttpStatus.valueOf(Integer.valueOf(hdr.getStatusCode()))).body(rsp);
	}
}
