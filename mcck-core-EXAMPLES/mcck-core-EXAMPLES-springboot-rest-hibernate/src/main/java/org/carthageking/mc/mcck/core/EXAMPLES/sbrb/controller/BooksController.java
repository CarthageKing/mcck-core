package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.controller;

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

import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.Book;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.CRUDBookResponse;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.GenericResponse;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.SearchBookResponse;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/books")
public class BooksController {

	private static final String MSG_OPERATION_SUCCESSFUL = "Operation successful";

	@Resource
	private BookService bookSvc;

	public BooksController() {
		// noop
	}

	@PostMapping(path = "/o", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	// The below annotation with CREATED value is needed if we want to change the default
	// HTTP OK response (which is 200) to some other value. For this, since we are creating
	// an object, then it would be more appropriate to return HTTP 201
	@ResponseStatus(HttpStatus.CREATED)
	public CRUDBookResponse createBook(@RequestBody Book book) {
		CRUDBookResponse rsp = new CRUDBookResponse();
		GenericResponse.GenericResponseHeader hdr = new GenericResponse.GenericResponseHeader();
		hdr.setStatusCode(String.valueOf(HttpStatus.CREATED.value()));
		hdr.setStatusMessage(MSG_OPERATION_SUCCESSFUL);
		rsp.setHeader(hdr);
		rsp.setData(bookSvc.createBook(book));
		return rsp;
	}

	@GetMapping(path = "/o/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public CRUDBookResponse retrieveBook(@PathVariable String id) {
		CRUDBookResponse rsp = new CRUDBookResponse();
		GenericResponse.GenericResponseHeader hdr = new GenericResponse.GenericResponseHeader();
		hdr.setStatusCode(String.valueOf(HttpStatus.OK.value()));
		hdr.setStatusMessage(MSG_OPERATION_SUCCESSFUL);
		rsp.setHeader(hdr);
		rsp.setData(bookSvc.retrieveBookById(id));
		return rsp;
	}

	@PutMapping(path = "/o/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public CRUDBookResponse updateBook(@PathVariable String id, @RequestBody Book book) {
		CRUDBookResponse rsp = new CRUDBookResponse();
		GenericResponse.GenericResponseHeader hdr = new GenericResponse.GenericResponseHeader();
		hdr.setStatusCode(String.valueOf(HttpStatus.OK.value()));
		hdr.setStatusMessage(MSG_OPERATION_SUCCESSFUL);
		rsp.setHeader(hdr);
		rsp.setData(bookSvc.updateBook(id, book));
		return rsp;
	}

	@DeleteMapping(path = "/o/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public CRUDBookResponse deleteBook(@PathVariable String id) {
		CRUDBookResponse rsp = new CRUDBookResponse();
		GenericResponse.GenericResponseHeader hdr = new GenericResponse.GenericResponseHeader();
		hdr.setStatusCode(String.valueOf(HttpStatus.OK.value()));
		hdr.setStatusMessage(MSG_OPERATION_SUCCESSFUL);
		rsp.setHeader(hdr);
		rsp.setData(bookSvc.deleteBook(id));
		return rsp;
	}

	@GetMapping(path = "/o", produces = MediaType.APPLICATION_JSON_VALUE)
	public SearchBookResponse searchBooksByGet(
		@RequestParam String nameStartsWith,
		@RequestParam String isbnContains,
		@RequestParam int atLeastNumPages,
		@RequestParam(required = false, defaultValue = "0") int pageNum,
		@RequestParam(required = false, defaultValue = "10") int numRecordsPerPage) {
		SearchBookResponse rsp = bookSvc.searchBooks(nameStartsWith, isbnContains, atLeastNumPages, pageNum, numRecordsPerPage);
		GenericResponse.GenericResponseHeader hdr = new GenericResponse.GenericResponseHeader();
		hdr.setStatusCode(String.valueOf(HttpStatus.OK.value()));
		hdr.setStatusMessage(MSG_OPERATION_SUCCESSFUL);
		rsp.setHeader(hdr);
		return rsp;
	}
}
