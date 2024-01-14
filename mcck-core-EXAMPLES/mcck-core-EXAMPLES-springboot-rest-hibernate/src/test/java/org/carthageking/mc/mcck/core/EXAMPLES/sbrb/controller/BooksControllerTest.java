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

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.apache.http.client.utils.URIBuilder;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.TestDbConfig;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.TestSpringConfig;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.config.CommonConfig;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.Book;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.CRUDBookResponse;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.SearchBookResponse;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperResult;
import org.carthageking.mc.mcck.core.json.McckJsonUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

import jakarta.annotation.Resource;

@ContextConfiguration(classes = { TestSpringConfig.class, CommonConfig.class, TestDbConfig.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BooksControllerTest {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BooksControllerTest.class);

	@Resource
	private HttpClientHelper httpClientHelper;

	@LocalServerPort
	private int port;

	private String baseUrl;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		baseUrl = "http://localhost:" + port;
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test_bookCRUD() {
		SearchBookResponse sbr = null;

		sbr = doSearchBooks("Mummy", "CAT", 200, 0, 20);
		Assertions.assertEquals(0, sbr.getData().getNumPages());
		Assertions.assertEquals(1, sbr.getData().getPageNum());
		Assertions.assertEquals(20, sbr.getData().getNumRecordsPerPage());
		Assertions.assertEquals(0, sbr.getData().getEntries().size());

		doCreateBook("Dummy Book for Dummies", "sdfsdf", 234, new Timestamp(OffsetDateTime.of(2018, 5, 6, 13, 14, 15, 0, ZoneOffset.UTC).toInstant().toEpochMilli()), "A book for dummies");

		sbr = doSearchBooks("Mummy", "CAT", 200, 0, 20);
		Assertions.assertEquals(0, sbr.getData().getNumPages());
		Assertions.assertEquals(1, sbr.getData().getPageNum());
		Assertions.assertEquals(20, sbr.getData().getNumRecordsPerPage());
		Assertions.assertEquals(0, sbr.getData().getEntries().size());

		sbr = doSearchBooks("Dummy", "fsd", 200, 0, 20);
		Assertions.assertEquals(1, sbr.getData().getNumPages());
		Assertions.assertEquals(1, sbr.getData().getPageNum());
		Assertions.assertEquals(20, sbr.getData().getNumRecordsPerPage());
		Assertions.assertEquals(1, sbr.getData().getEntries().size());

		Book aBook = doCreateBook("Mummy 1", "CATastrophe", 199, null, "description");
		doCreateBook("Mummy 2", "CATastrophek", 200, null, "description");
		doCreateBook("Mummy 3", "rataCAT", 536, null, "description");
		doCreateBook("Mummy 4", "marCATjoe", 212, null, "description");

		sbr = doSearchBooks("Mummy", "CAT", 200, 0, 20);
		Assertions.assertEquals(1, sbr.getData().getNumPages());
		Assertions.assertEquals(1, sbr.getData().getPageNum());
		Assertions.assertEquals(20, sbr.getData().getNumRecordsPerPage());
		Assertions.assertEquals(3, sbr.getData().getEntries().size());
		Assertions.assertEquals(true, getBookWithName("Mummy 2", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(true, getBookWithName("Mummy 3", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(true, getBookWithName("Mummy 4", sbr.getData().getEntries()).isPresent());

		sbr = doSearchBooks("Mummy", "CAT", 200, -1, 3);
		Assertions.assertEquals(1, sbr.getData().getNumPages());
		Assertions.assertEquals(1, sbr.getData().getPageNum());
		Assertions.assertEquals(3, sbr.getData().getNumRecordsPerPage());
		Assertions.assertEquals(3, sbr.getData().getEntries().size());
		Assertions.assertEquals(true, getBookWithName("Mummy 2", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(true, getBookWithName("Mummy 3", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(true, getBookWithName("Mummy 4", sbr.getData().getEntries()).isPresent());

		sbr = doSearchBooks("Mummy", "CAT", 200, 1, 2);
		Assertions.assertEquals(2, sbr.getData().getNumPages());
		Assertions.assertEquals(1, sbr.getData().getPageNum());
		Assertions.assertEquals(2, sbr.getData().getNumRecordsPerPage());
		Assertions.assertEquals(2, sbr.getData().getEntries().size());
		Assertions.assertEquals(true, getBookWithName("Mummy 2", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(true, getBookWithName("Mummy 3", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(false, getBookWithName("Mummy 4", sbr.getData().getEntries()).isPresent());

		sbr = doSearchBooks("Mummy", "CAT", 200, 5, 2);
		Assertions.assertEquals(2, sbr.getData().getNumPages());
		Assertions.assertEquals(2, sbr.getData().getPageNum());
		Assertions.assertEquals(2, sbr.getData().getNumRecordsPerPage());
		Assertions.assertEquals(1, sbr.getData().getEntries().size());
		Assertions.assertEquals(false, getBookWithName("Mummy 2", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(false, getBookWithName("Mummy 3", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(true, getBookWithName("Mummy 4", sbr.getData().getEntries()).isPresent());

		aBook.setNumPages(210);
		Book updatedBook = doUpdateBook(aBook);

		sbr = doSearchBooks("Mummy", "CAT", 200, 0, 20);
		Assertions.assertEquals(1, sbr.getData().getNumPages());
		Assertions.assertEquals(1, sbr.getData().getPageNum());
		Assertions.assertEquals(20, sbr.getData().getNumRecordsPerPage());
		Assertions.assertEquals(4, sbr.getData().getEntries().size());
		Assertions.assertEquals(true, getBookWithName("Mummy 1", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(true, getBookWithName("Mummy 2", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(true, getBookWithName("Mummy 3", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(true, getBookWithName("Mummy 4", sbr.getData().getEntries()).isPresent());

		Book someBook = getBookById(updatedBook.getId());
		Assertions.assertEquals("Mummy 1", someBook.getName());

		Book deletedBook = deleteBook(someBook.getId());
		Assertions.assertEquals("Mummy 1", deletedBook.getName());

		sbr = doSearchBooks("Mummy", "CAT", 200, 0, 20);
		Assertions.assertEquals(1, sbr.getData().getNumPages());
		Assertions.assertEquals(1, sbr.getData().getPageNum());
		Assertions.assertEquals(20, sbr.getData().getNumRecordsPerPage());
		Assertions.assertEquals(3, sbr.getData().getEntries().size());
		Assertions.assertEquals(false, getBookWithName("Mummy 1", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(true, getBookWithName("Mummy 2", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(true, getBookWithName("Mummy 3", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(true, getBookWithName("Mummy 4", sbr.getData().getEntries()).isPresent());

		// try to get the deleted book
		{
			HttpClientHelperResult<String> result = httpClientHelper.doGet(HttpClientHelper.createURI(() -> {
				URIBuilder builder = new URIBuilder(baseUrl + "/books/o/" + deletedBook.getId());
				return builder.build();
			}));
			LOG.trace("the response: {}", result.getBodyAsString());
			Assertions.assertEquals(HttpStatus.NOT_FOUND, HttpStatus.valueOf(result.getStatusLine().getCode()));
			CRUDBookResponse rspObj = McckJsonUtil.toObject(result.getBodyAsString(), CRUDBookResponse.class);
			Assertions.assertEquals(String.valueOf(HttpStatus.NOT_FOUND.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
			Assertions.assertEquals(null, rspObj.getData());
		}
	}

	private Optional<Book> getBookWithName(String name, List<Book> entries) {
		for (Book b : entries) {
			if (b.getName().equals(name)) {
				return Optional.of(b);
			}
		}
		return Optional.empty();
	}

	private SearchBookResponse doSearchBooks(String nameStartsWith, String isbnContains, int atLeastNumPages, int pageNum, int numRecordsPerPage) {
		HttpClientHelperResult<String> result = httpClientHelper.doGet(HttpClientHelper.createURI(() -> {
			URIBuilder builder = new URIBuilder(baseUrl + "/books/o");
			builder.addParameter("nameStartsWith", nameStartsWith);
			builder.addParameter("isbnContains", isbnContains);
			builder.addParameter("atLeastNumPages", String.valueOf(atLeastNumPages));
			builder.addParameter("pageNum", String.valueOf(pageNum));
			builder.addParameter("numRecordsPerPage", String.valueOf(numRecordsPerPage));
			return builder.build();
		}));
		LOG.trace("the response: {}", result.getBodyAsString());
		Assertions.assertEquals(HttpStatus.OK, HttpStatus.valueOf(result.getStatusLine().getCode()));
		SearchBookResponse rspObj = McckJsonUtil.toObject(result.getBodyAsString(), SearchBookResponse.class);
		Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
		Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
		return rspObj;
	}

	private Book doCreateBook(String name, String isbn, int numPages, Timestamp revisionDateTime, String description) {
		String initialId = "changeme";
		Book book = new Book();
		book.setId(initialId);
		book.setIsbn(isbn);
		book.setName(name);
		book.setNumPages(numPages);
		book.setRevisionDateTime(revisionDateTime);
		book.setDescription(description);
		String content = McckJsonUtil.toStr(book);
		HttpClientHelperResult<String> result = httpClientHelper.doPost(HttpClientHelper.createURI(() -> {
			URIBuilder builder = new URIBuilder(baseUrl + "/books/o");
			return builder.build();
		}), content);
		LOG.trace("the response: {}", result.getBodyAsString());
		Assertions.assertEquals(HttpStatus.CREATED, HttpStatus.valueOf(result.getStatusLine().getCode()));
		CRUDBookResponse rspObj = McckJsonUtil.toObject(result.getBodyAsString(), CRUDBookResponse.class);
		Assertions.assertEquals(String.valueOf(HttpStatus.CREATED.value()), rspObj.getHeader().getStatusCode());
		Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
		Book bkr = rspObj.getData();
		Assertions.assertNotEquals(initialId, bkr.getId());
		Assertions.assertEquals(book.getIsbn(), bkr.getIsbn());
		Assertions.assertEquals(book.getName(), bkr.getName());
		Assertions.assertEquals(book.getNumPages(), bkr.getNumPages());
		Assertions.assertEquals(book.getRevisionDateTime(), bkr.getRevisionDateTime());
		Assertions.assertEquals(book.getDescription(), bkr.getDescription());

		return bkr;
	}

	private Book doUpdateBook(Book aBook) {
		String content = McckJsonUtil.toStr(aBook);
		HttpClientHelperResult<String> result = httpClientHelper.doPut(HttpClientHelper.createURI(() -> {
			URIBuilder builder = new URIBuilder(baseUrl + "/books/o/" + aBook.getId());
			return builder.build();
		}), content);
		LOG.trace("the response: {}", result.getBodyAsString());
		Assertions.assertEquals(HttpStatus.OK, HttpStatus.valueOf(result.getStatusLine().getCode()));
		CRUDBookResponse rspObj = McckJsonUtil.toObject(result.getBodyAsString(), CRUDBookResponse.class);
		Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
		Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
		Book bkr = rspObj.getData();
		Assertions.assertEquals(aBook.getId(), bkr.getId());
		Assertions.assertEquals(aBook.getIsbn(), bkr.getIsbn());
		Assertions.assertEquals(aBook.getName(), bkr.getName());
		Assertions.assertEquals(aBook.getNumPages(), bkr.getNumPages());
		Assertions.assertEquals(aBook.getRevisionDateTime(), bkr.getRevisionDateTime());
		Assertions.assertEquals(aBook.getDescription(), bkr.getDescription());

		return bkr;
	}

	private Book getBookById(String id) {
		HttpClientHelperResult<String> result = httpClientHelper.doGet(HttpClientHelper.createURI(() -> {
			URIBuilder builder = new URIBuilder(baseUrl + "/books/o/" + id);
			return builder.build();
		}));
		LOG.trace("the response: {}", result.getBodyAsString());
		Assertions.assertEquals(HttpStatus.OK, HttpStatus.valueOf(result.getStatusLine().getCode()));
		CRUDBookResponse rspObj = McckJsonUtil.toObject(result.getBodyAsString(), CRUDBookResponse.class);
		Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
		Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
		Book bkr = rspObj.getData();
		return bkr;
	}

	private Book deleteBook(String id) {
		HttpClientHelperResult<String> result = httpClientHelper.doDelete(HttpClientHelper.createURI(() -> {
			URIBuilder builder = new URIBuilder(baseUrl + "/books/o/" + id);
			return builder.build();
		}));
		LOG.trace("the response: {}", result.getBodyAsString());
		Assertions.assertEquals(HttpStatus.OK, HttpStatus.valueOf(result.getStatusLine().getCode()));
		CRUDBookResponse rspObj = McckJsonUtil.toObject(result.getBodyAsString(), CRUDBookResponse.class);
		Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
		Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
		Book bkr = rspObj.getData();
		return bkr;
	}
}
