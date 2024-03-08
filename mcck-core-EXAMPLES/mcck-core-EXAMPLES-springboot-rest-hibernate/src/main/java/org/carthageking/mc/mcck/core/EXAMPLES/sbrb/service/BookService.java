package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.service;

import java.util.Optional;

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

import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao.BookEntityDao;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao.BookEntitySearchDao;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao.entity.BookEntity;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.exception.AppCustomResultNotFoundException;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.Book;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.SearchBookResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;

@Service
public class BookService {

	@Resource
	private BookEntityDao bookDao;

	@Resource
	private BookEntitySearchDao bookSearchDao;

	public BookService() {
		// noop
	}

	@Caching(evict = {
		@CacheEvict(value = { "app_custom.retrieveBookById" }, allEntries = true),
		@CacheEvict(value = { "app_custom.searchBooks" }, allEntries = true),
	})
	@Transactional
	public Book createBook(Book book) {
		BookEntity be = convertToBookEntity(book);
		BookEntity ber = bookDao.saveAndFlush(be);
		return convertToBook(ber);
	}

	@Cacheable("app_custom.retrieveBookById")
	public Book retrieveBookById(String id) {
		Optional<BookEntity> theReturn = bookDao.findById(id);
		if (theReturn.isEmpty()) {
			throw new AppCustomResultNotFoundException("Unable to find a book with that ID");
		}
		return convertToBook(theReturn.get());
	}

	@Caching(evict = {
		@CacheEvict(value = { "app_custom.retrieveBookById" }, allEntries = true),
		@CacheEvict(value = { "app_custom.searchBooks" }, allEntries = true),
	})
	@Transactional
	public Book updateBook(String id, Book book) {
		Optional<BookEntity> theReturn = bookDao.findById(id);
		if (theReturn.isEmpty()) {
			throw new AppCustomResultNotFoundException("Unable to find a book with that ID");
		}
		BookEntity exist = theReturn.get();
		copyFromBookToBookEntity(book, exist);
		exist.setId(id);
		return convertToBook(bookDao.saveAndFlush(exist));
	}

	@Caching(evict = {
		@CacheEvict(value = { "app_custom.retrieveBookById" }, allEntries = true),
		@CacheEvict(value = { "app_custom.searchBooks" }, allEntries = true),
	})
	@Transactional
	public Book deleteBook(String id) {
		Optional<BookEntity> theReturn = bookDao.findById(id);
		if (theReturn.isEmpty()) {
			throw new AppCustomResultNotFoundException("Unable to find a book with that ID");
		}
		BookEntity exist = theReturn.get();
		Book toReturn = convertToBook(exist);
		bookDao.delete(exist);
		return toReturn;
	}

	@Cacheable("app_custom.searchBooks")
	@Transactional
	public SearchBookResponse searchBooks(String nameStartsWith,
		String isbnContains, int atLeastNumPages, int pageNum,
		int numRecordsPerPage) {
		return bookSearchDao.searchBooks(nameStartsWith, isbnContains, atLeastNumPages, pageNum, numRecordsPerPage);
	}

	private BookEntity convertToBookEntity(Book book) {
		BookEntity be = new BookEntity();
		copyFromBookToBookEntity(book, be);
		return be;
	}

	private void copyFromBookToBookEntity(Book book, BookEntity be) {
		be.setDescription(book.getDescription());
		be.setIsbn(book.getIsbn());
		be.setName(book.getName());
		be.setNumPages(book.getNumPages());
		be.setRevisionDateTime(book.getRevisionDateTime());
	}

	public static Book convertToBook(BookEntity ber) {
		Book createdBook = new Book();
		createdBook.setDescription(ber.getDescription());
		createdBook.setId(ber.getId());
		createdBook.setIsbn(ber.getIsbn());
		createdBook.setName(ber.getName());
		createdBook.setNumPages(ber.getNumPages());
		createdBook.setRevisionDateTime(ber.getRevisionDateTime());
		return createdBook;
	}
}
