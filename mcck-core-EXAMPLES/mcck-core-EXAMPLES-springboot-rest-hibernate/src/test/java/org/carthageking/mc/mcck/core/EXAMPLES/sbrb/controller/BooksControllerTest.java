package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.controller;

import java.nio.charset.StandardCharsets;

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
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpHeaders;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.TestDbConfig;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.TestSpringConfig;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.config.CommonConfig;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao.entity.BookEntity;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.Book;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.CRUDBookResponse;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.SearchBookResponse;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperResult;
import org.carthageking.mc.mcck.core.json.McckJsonUtil;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.internal.property.RevisionNumberPropertyName;
import org.hibernate.envers.query.order.internal.PropertyAuditOrder;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@ContextConfiguration(classes = { TestSpringConfig.class, CommonConfig.class, TestDbConfig.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BooksControllerTest {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BooksControllerTest.class);

	@Resource
	private HttpClientHelper httpClientHelper;

	@LocalServerPort
	private int port;

	private String baseUrl;

	// autowiring the existing EntityManager due to the system complaining that the 'session was closed' did not work.
	// this is a similar problem as described in https://stackoverflow.com/a/77023617 and the solution there was used to fix the problem
	@Resource
	private EntityManagerFactory entityMgrFactory;

	private EntityManager entityMgr;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		baseUrl = "http://localhost:" + port;
		entityMgr = entityMgrFactory.createEntityManager();
	}

	@AfterEach
	void tearDown() throws Exception {
		entityMgr.close();
	}

	@SuppressWarnings("unchecked")
	@Test
	void test_bookCRUD() {
		AuditReader auditReader = AuditReaderFactory.get(entityMgr);
		SearchBookResponse sbr = null;
		List<BookEntity> revisions = null;
		String creationUsername = "creator@example.org";
		String modificationUsername = "modder@example.org";

		sbr = doSearchBooks("Mummy", "CAT", 200, 0, 20);
		Assertions.assertEquals(0, sbr.getData().getNumPages());
		Assertions.assertEquals(1, sbr.getData().getPageNum());
		Assertions.assertEquals(20, sbr.getData().getNumRecordsPerPage());
		Assertions.assertEquals(0, sbr.getData().getEntries().size());

		// no audit records at this point
		revisions = auditReader.createQuery().forRevisionsOfEntity(BookEntity.class, true, true).addOrder(new PropertyAuditOrder(null, new RevisionNumberPropertyName(), true)).getResultList();
		Assertions.assertEquals(0, revisions.size());

		doCreateBook("Dummy Book for Dummies", "sdfsdf", 234, new Timestamp(OffsetDateTime.of(2018, 5, 6, 13, 14, 15, 0, ZoneOffset.UTC).toInstant().toEpochMilli()), "A book for dummies");

		// created a record, so we have one revision
		revisions = auditReader.createQuery().forRevisionsOfEntity(BookEntity.class, true, true).addOrder(new PropertyAuditOrder(null, new RevisionNumberPropertyName(), true)).getResultList();
		Assertions.assertEquals(1, revisions.size());
		assertAllHaveCreatedandModifiedDateTime(revisions);
		Assertions.assertNull(revisions.get(0).getCreatedBy());
		Assertions.assertNull(revisions.get(0).getLastModifiedBy());

		sbr = doSearchBooks("Mummy", "CAT", 200, 0, 20);
		Assertions.assertEquals(0, sbr.getData().getNumPages());
		Assertions.assertEquals(1, sbr.getData().getPageNum());
		Assertions.assertEquals(20, sbr.getData().getNumRecordsPerPage());
		Assertions.assertEquals(0, sbr.getData().getEntries().size());

		sbr = doSearchBooksByPost("Mummy", "CAT", 200, 0, 20);
		Assertions.assertEquals(0, sbr.getData().getNumPages());
		Assertions.assertEquals(1, sbr.getData().getPageNum());
		Assertions.assertEquals(20, sbr.getData().getNumRecordsPerPage());
		Assertions.assertEquals(0, sbr.getData().getEntries().size());

		// reads don't cause a new revision
		revisions = auditReader.createQuery().forRevisionsOfEntity(BookEntity.class, true, true).addOrder(new PropertyAuditOrder(null, new RevisionNumberPropertyName(), true)).getResultList();
		Assertions.assertEquals(1, revisions.size());

		sbr = doSearchBooks("Dummy", "fsd", 200, 0, 20);
		Assertions.assertEquals(1, sbr.getData().getNumPages());
		Assertions.assertEquals(1, sbr.getData().getPageNum());
		Assertions.assertEquals(20, sbr.getData().getNumRecordsPerPage());
		Assertions.assertEquals(1, sbr.getData().getEntries().size());

		// reads don't cause a new revision
		revisions = auditReader.createQuery().forRevisionsOfEntity(BookEntity.class, true, true).addOrder(new PropertyAuditOrder(null, new RevisionNumberPropertyName(), true)).getResultList();
		Assertions.assertEquals(1, revisions.size());
		Assertions.assertEquals(1, revisions.size());
		assertAllHaveCreatedandModifiedDateTime(revisions);
		Assertions.assertNull(revisions.get(0).getCreatedBy());
		Assertions.assertNull(revisions.get(0).getLastModifiedBy());

		Book aBook = doCreateBook("Mummy 1", "CATastrophe", 199, null, "description", creationUsername);
		doCreateBook("Mummy 2", "CATastrophek", 200, null, "description", creationUsername);
		doCreateBook("Mummy 3", "rataCAT", 536, null, "description", creationUsername);
		doCreateBook("Mummy 4", "marCATjoe", 212, null, "description", creationUsername);

		// created 4 more records
		revisions = auditReader.createQuery().forRevisionsOfEntity(BookEntity.class, true, true).addOrder(new PropertyAuditOrder(null, new RevisionNumberPropertyName(), true)).getResultList();
		Assertions.assertEquals(5, revisions.size());
		assertAllHaveCreatedandModifiedDateTime(revisions);
		// the first record doesn't have any identified creators or modifiers
		Assertions.assertNull(revisions.get(0).getCreatedBy());
		Assertions.assertNull(revisions.get(0).getLastModifiedBy());
		// the remaining ones have
		for (int i = 1; i < revisions.size(); i++) {
			BookEntity be = revisions.get(i);
			Assertions.assertEquals(creationUsername, be.getCreatedBy());
			Assertions.assertEquals(creationUsername, be.getLastModifiedBy());
		}

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

		sbr = doSearchBooksByPost("Mummy", "CAT", 200, 5, 2);
		Assertions.assertEquals(2, sbr.getData().getNumPages());
		Assertions.assertEquals(2, sbr.getData().getPageNum());
		Assertions.assertEquals(2, sbr.getData().getNumRecordsPerPage());
		Assertions.assertEquals(1, sbr.getData().getEntries().size());
		Assertions.assertEquals(false, getBookWithName("Mummy 2", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(false, getBookWithName("Mummy 3", sbr.getData().getEntries()).isPresent());
		Assertions.assertEquals(true, getBookWithName("Mummy 4", sbr.getData().getEntries()).isPresent());

		revisions = auditReader.createQuery().forRevisionsOfEntity(BookEntity.class, true, true).addOrder(new PropertyAuditOrder(null, new RevisionNumberPropertyName(), true)).getResultList();
		Assertions.assertEquals(5, revisions.size());
		assertAllHaveCreatedandModifiedDateTime(revisions);
		// the first record doesn't have any identified creators or modifiers
		Assertions.assertNull(revisions.get(0).getCreatedBy());
		Assertions.assertNull(revisions.get(0).getLastModifiedBy());
		// the remaining ones have
		for (int i = 1; i < revisions.size(); i++) {
			BookEntity be = revisions.get(i);
			Assertions.assertEquals(creationUsername, be.getCreatedBy());
			Assertions.assertEquals(creationUsername, be.getLastModifiedBy());
		}

		{
			entityMgr.clear();
			BookEntity be = entityMgr.find(BookEntity.class, aBook.getId());
			Assertions.assertEquals(creationUsername, be.getCreatedBy());
			Assertions.assertEquals(creationUsername, be.getLastModifiedBy());
			Assertions.assertNotNull(be.getCreatedDateTime());
			Assertions.assertEquals(be.getCreatedDateTime(), be.getLastModifiedDateTime());
		}

		Assertions.assertEquals(199, aBook.getNumPages());
		aBook.setNumPages(210);
		Assertions.assertEquals(210, aBook.getNumPages());
		Book updatedBook = doUpdateBook(aBook, modificationUsername);
		Assertions.assertEquals(210, aBook.getNumPages());
		Assertions.assertEquals(210, updatedBook.getNumPages());

		// after book entity update, check the modification info
		{
			entityMgr.clear();
			BookEntity be = entityMgr.find(BookEntity.class, aBook.getId());
			Assertions.assertEquals(creationUsername, be.getCreatedBy());
			Assertions.assertEquals(modificationUsername, be.getLastModifiedBy());
			Assertions.assertNotNull(be.getCreatedDateTime());
			Assertions.assertNotEquals(be.getCreatedDateTime(), be.getLastModifiedDateTime());
		}

		// a record was updated, which results in a new revision
		revisions = auditReader.createQuery().forRevisionsOfEntity(BookEntity.class, true, true).addOrder(new PropertyAuditOrder(null, new RevisionNumberPropertyName(), true)).getResultList();
		Assertions.assertEquals(6, revisions.size());

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

		revisions = auditReader.createQuery().forRevisionsOfEntity(BookEntity.class, true, true).addOrder(new PropertyAuditOrder(null, new RevisionNumberPropertyName(), true)).getResultList();
		Assertions.assertEquals(6, revisions.size());
		// the first record doesn't have any identified creators or modifiers
		Assertions.assertNull(revisions.get(0).getCreatedBy());
		Assertions.assertNull(revisions.get(0).getLastModifiedBy());
		// the remaining ones have
		for (int i = 1; i < revisions.size() - 1; i++) {
			BookEntity be = revisions.get(i);
			Assertions.assertEquals(creationUsername, be.getCreatedBy());
			Assertions.assertEquals(creationUsername, be.getLastModifiedBy());
		}
		// the latest entry now has different creation and modification users
		Assertions.assertEquals(creationUsername, revisions.get(revisions.size() - 1).getCreatedBy());
		Assertions.assertEquals(modificationUsername, revisions.get(revisions.size() - 1).getLastModifiedBy());

		Book deletedBook = deleteBook(someBook.getId());
		Assertions.assertEquals("Mummy 1", deletedBook.getName());

		// deletes cause a new revision
		revisions = auditReader.createQuery().forRevisionsOfEntity(BookEntity.class, true, true).addOrder(new PropertyAuditOrder(null, new RevisionNumberPropertyName(), true)).getResultList();
		Assertions.assertEquals(7, revisions.size());
		// the first record doesn't have any identified creators or modifiers
		Assertions.assertNull(revisions.get(0).getCreatedBy());
		Assertions.assertNull(revisions.get(0).getLastModifiedBy());
		// the remaining ones have
		for (int i = 1; i < revisions.size() - 2; i++) {
			BookEntity be = revisions.get(i);
			Assertions.assertEquals(creationUsername, be.getCreatedBy());
			Assertions.assertEquals(creationUsername, be.getLastModifiedBy());
		}
		// the second-to-the-last and last entries now has different creation and modification users
		Assertions.assertEquals(creationUsername, revisions.get(revisions.size() - 2).getCreatedBy());
		Assertions.assertEquals(modificationUsername, revisions.get(revisions.size() - 2).getLastModifiedBy());
		Assertions.assertEquals(creationUsername, revisions.get(revisions.size() - 1).getCreatedBy());
		Assertions.assertEquals(modificationUsername, revisions.get(revisions.size() - 1).getLastModifiedBy());

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

		revisions = auditReader.createQuery().forRevisionsOfEntity(BookEntity.class, true, true).addOrder(new PropertyAuditOrder(null, new RevisionNumberPropertyName(), true)).getResultList();
		Assertions.assertEquals(7, revisions.size());
		// the first record doesn't have any identified creators or modifiers
		Assertions.assertNull(revisions.get(0).getCreatedBy());
		Assertions.assertNull(revisions.get(0).getLastModifiedBy());
		// the remaining ones have
		for (int i = 1; i < revisions.size() - 2; i++) {
			BookEntity be = revisions.get(i);
			Assertions.assertEquals(creationUsername, be.getCreatedBy());
			Assertions.assertEquals(creationUsername, be.getLastModifiedBy());
		}
		// the second-to-the-last and last entries now has different creation and modification users
		Assertions.assertEquals(creationUsername, revisions.get(revisions.size() - 2).getCreatedBy());
		Assertions.assertEquals(modificationUsername, revisions.get(revisions.size() - 2).getLastModifiedBy());
		Assertions.assertEquals(creationUsername, revisions.get(revisions.size() - 1).getCreatedBy());
		Assertions.assertEquals(modificationUsername, revisions.get(revisions.size() - 1).getLastModifiedBy());
	}

	private void assertAllHaveCreatedandModifiedDateTime(List<BookEntity> revisions) {
		for (int i = 0; i < revisions.size(); i++) {
			BookEntity be = revisions.get(i);
			Assertions.assertNotNull(be.getCreatedDateTime(), "object at index " + i + " has null creation datetime");
			Assertions.assertNotNull(be.getLastModifiedDateTime(), "object at index " + i + " has null last modified datetime");
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

	private SearchBookResponse doSearchBooksByPost(String nameStartsWith, String isbnContains, int atLeastNumPages, int pageNum, int numRecordsPerPage) {
		String content = HttpClientHelper.createUrlEncoded(Arrays.asList(
			new BasicNameValuePair("nameStartsWith", nameStartsWith),
			new BasicNameValuePair("isbnContains", isbnContains),
			new BasicNameValuePair("atLeastNumPages", String.valueOf(atLeastNumPages)),
			new BasicNameValuePair("pageNum", String.valueOf(pageNum)),
			new BasicNameValuePair("numRecordsPerPage", String.valueOf(numRecordsPerPage))));
		HttpClientHelperResult<String> result = httpClientHelper.doPost(HttpClientHelper.createURI(() -> {
			URIBuilder builder = new URIBuilder(baseUrl + "/books/_search");
			return builder.build();
		}), content, hdr -> {
			hdr.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
			hdr.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		});
		LOG.trace("the response: {}", result.getBodyAsString());
		Assertions.assertEquals(HttpStatus.OK, HttpStatus.valueOf(result.getStatusLine().getCode()));
		SearchBookResponse rspObj = McckJsonUtil.toObject(result.getBodyAsString(), SearchBookResponse.class);
		Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
		Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
		return rspObj;
	}

	private Book doCreateBook(String name, String isbn, int numPages, Timestamp revisionDateTime, String description) {
		return doCreateBook(name, isbn, numPages, revisionDateTime, description, null);
	}

	private Book doCreateBook(String name, String isbn, int numPages, Timestamp revisionDateTime, String description, String jwtUsername) {
		String initialId = "changeme";
		Book book = new Book();
		book.setId(initialId);
		book.setIsbn(isbn);
		book.setName(name);
		book.setNumPages(numPages);
		book.setRevisionDateTime(revisionDateTime);
		book.setDescription(description);
		String content = McckJsonUtil.toStr(book);
		HttpClientHelperResult<String> result = null;

		if (null == jwtUsername) {
			result = httpClientHelper.doPost(HttpClientHelper.createURI(() -> {
				URIBuilder builder = new URIBuilder(baseUrl + "/books/o");
				return builder.build();
			}), content);
		} else {
			result = httpClientHelper.doPost(HttpClientHelper.createURI(() -> {
				URIBuilder builder = new URIBuilder(baseUrl + "/books/o");
				return builder.build();
			}), content, hdr -> {
				hdr.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
				hdr.setHeader(HttpHeaders.AUTHORIZATION, createMinimalJwt(jwtUsername));
			});
		}
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

	private Book doUpdateBook(Book aBook, String jwtUsername) {
		String content = McckJsonUtil.toStr(aBook);
		HttpClientHelperResult<String> result = httpClientHelper.doPut(HttpClientHelper.createURI(() -> {
			URIBuilder builder = new URIBuilder(baseUrl + "/books/o/" + aBook.getId());
			return builder.build();
		}), content, hdr -> {
			hdr.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			hdr.setHeader(HttpHeaders.AUTHORIZATION, createMinimalJwt(jwtUsername));
		});
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

	private String createMinimalJwt(String jwtUsername) {
		String jwt = "{\"" + OAuthFilter.CLAIM_USERNAME + "\": \"" + jwtUsername + "\"}";
		return OAuthFilter.BEARER_TOKEN + Base64.getEncoder().encodeToString(jwt.getBytes(StandardCharsets.UTF_8));
	}
}
