package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao.entity;

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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "book", indexes = {
	@Index(columnList = "b_isbn", name = "UIDX_ISBN", unique = true),
	@Index(columnList = "b_numpages", name = "IDX_NUMPAGES")
})
public class BookEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "b_id", length = 40)
	private String id;

	@Column(name = "b_name")
	private String name;

	@Column(name = "b_isbn", length = 20)
	private String isbn;

	@Column(name = "b_revisiondt")
	private Timestamp revisionDateTime;

	@Column(name = "b_numpages")
	private int numPages;

	@Column(name = "b_description")
	private String description;

	public BookEntity() {
		// noop
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Timestamp getRevisionDateTime() {
		return revisionDateTime;
	}

	public void setRevisionDateTime(Timestamp revisionDateTime) {
		this.revisionDateTime = revisionDateTime;
	}

	public int getNumPages() {
		return numPages;
	}

	public void setNumPages(int numPages) {
		this.numPages = numPages;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
