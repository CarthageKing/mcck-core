package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao.entity;

import java.sql.Date;

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

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "book", indexes = {
	@Index(columnList = "b_isbn", name = "UIDX_ISBN", unique = true),
	@Index(columnList = "b_numpages", name = "IDX_NUMPAGES")
})
// annotation below tells Spring/Envers to generate the corresponding audit
// table for this entity
@Audited
// since the entity below gets some common properties from a mapped superclass
// and we want those properties to also appear in the generated audit table
// as columns, we include the below annotation
@AuditOverride(forClass = AuditableEntity.class)
// since our entity has some fields with the CreatedBy, CreatedDate,
// LastModifiedBy and LastModifiedDate annotations, the annotation below
// with the specific value is needed to autopopulate those. For this, we will
// also need an AuditorAware implementation to populate the *By fields
@EntityListeners(AuditingEntityListener.class)
public class BookEntity extends AuditableEntity {

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

	// without @Lob but specifying max length for string should tell
	// Hibernate to create a text column
	@Column(name = "b_excerpt", length = Integer.MAX_VALUE)
	private String excerpt;

	// use of @Lob below + specifying max length should tell Hibernate to
	// create a blob/clob/oid column
	@Column(name = "b_img", length = Integer.MAX_VALUE)
	@Lob
	private String base64Image;

	@Column(name = "b_hash")
	private Long hash;

	// use java.sql.Date class to tell Hibernate to generate a 'date' column.
	// do not use 'java.util.Date'
	@Column(name = "b_date_published")
	private Date datePublished;

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

	public String getExcerpt() {
		return excerpt;
	}

	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}

	public String getBase64Image() {
		return base64Image;
	}

	public void setBase64Image(String base64Image) {
		this.base64Image = base64Image;
	}

	public Long getHash() {
		return hash;
	}

	public void setHash(Long hash) {
		this.hash = hash;
	}

	public Date getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}
}
