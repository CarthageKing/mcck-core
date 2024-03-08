package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao;

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

import java.util.ArrayList;
import java.util.List;

import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao.entity.BookEntity;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.Book;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.PaginatedResponseContainer;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.SearchBookResponse;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.service.BookService;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@Repository
public class BookEntitySearchDao {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BookEntitySearchDao.class);

	@PersistenceContext
	private EntityManager em;

	public BookEntitySearchDao() {
		// noop
	}

	public SearchBookResponse searchBooks(String nameStartsWith, String isbnContains, int atLeastNumPages, int pageNum, int numRecordsPerPage) {
		String whereClause = buildWhereClause("a", nameStartsWith, isbnContains, atLeastNumPages);
		String countQueryStr = "select count(*) from " + BookEntity.class.getSimpleName() + " a " + whereClause;
		LOG.trace("The count query was: {}", countQueryStr);
		Query countQuery = em.createQuery(countQueryStr);
		if (null != nameStartsWith) {
			countQuery.setParameter("name", nameStartsWith + "%");
		}
		if (null != isbnContains) {
			countQuery.setParameter("isbn", "%" + isbnContains + "%");
		}
		countQuery.setParameter("numPages", atLeastNumPages);
		// FIXME: convert to long
		int totalRecords = ((Long) countQuery.getSingleResult()).intValue();
		int totalPages = totalRecords / numRecordsPerPage;
		if (0 != totalRecords % numRecordsPerPage) {
			totalPages++;
		}

		if (pageNum > totalPages) {
			pageNum = totalPages;
		}
		if (pageNum < 1) {
			pageNum = 1;
		}

		String finalQueryStr = "from " + BookEntity.class.getSimpleName() + " a " + whereClause + " order by a.name asc";
		LOG.trace("The final query was: {}", finalQueryStr);
		TypedQuery<BookEntity> finalQuery = em.createQuery(finalQueryStr, BookEntity.class);
		if (null != nameStartsWith) {
			finalQuery.setParameter("name", nameStartsWith + "%");
		}
		if (null != isbnContains) {
			finalQuery.setParameter("isbn", "%" + isbnContains + "%");
		}
		finalQuery.setParameter("numPages", atLeastNumPages);
		finalQuery.setFirstResult((pageNum - 1) * numRecordsPerPage);
		finalQuery.setMaxResults(numRecordsPerPage);
		List<BookEntity> outlst = finalQuery.getResultList();

		SearchBookResponse rsp = new SearchBookResponse();
		PaginatedResponseContainer<Book> entries = new PaginatedResponseContainer<>();
		for (BookEntity be : outlst) {
			entries.getEntries().add(BookService.convertToBook(be));
		}
		entries.setNumPages(totalPages);
		entries.setNumRecordsPerPage(numRecordsPerPage);
		entries.setPageNum(pageNum);
		rsp.setData(entries);
		return rsp;
	}

	private String buildWhereClause(String tableAlias, String nameStartsWith, String isbnContains, int atLeastNumPages) {
		List<String> parts = new ArrayList<>();
		if (null != nameStartsWith) {
			parts.add(tableAlias + ".name like :name");
		}
		if (null != isbnContains) {
			parts.add(tableAlias + ".isbn like :isbn");
		}
		parts.add(tableAlias + ".numPages>=:numPages");

		StringBuilder sb = new StringBuilder("where ");
		boolean first = true;
		for (String part : parts) {
			if (!first) {
				sb.append(" and ");
			}
			sb.append(part);
			first = false;
		}

		return sb.toString();
	}
}
