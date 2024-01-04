package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.service;

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
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao.entity.BookEntity;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model.Book;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;

@Service
public class BookService {

	@Resource
	private BookEntityDao bookDao;

	public BookService() {
		// noop
	}

	@Transactional
	public Book createBook(Book book) {
		BookEntity be = new BookEntity();
		be.setDescription(book.getDescription());
		be.setIsbn(book.getIsbn());
		be.setName(book.getName());
		be.setNumPages(book.getNumPages());
		be.setRevisionDateTime(book.getRevisionDateTime());

		BookEntity ber = bookDao.saveAndFlush(be);

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
