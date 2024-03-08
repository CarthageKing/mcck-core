package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.model;

import java.util.ArrayList;
import java.util.List;

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

@JsonInclude(Include.NON_EMPTY)
public class PaginatedResponseContainer<T extends java.io.Serializable> implements java.io.Serializable {

	private static final long serialVersionUID = -4047270518273233870L;

	private int numPages;
	private int numRecordsPerPage;
	private int pageNum;

	private String first;
	private String next;
	private String prev;
	private String last;

	private List<T> entries = new ArrayList<>();

	public PaginatedResponseContainer() {
		// noop
	}

	public int getNumPages() {
		return numPages;
	}

	public void setNumPages(int numPages) {
		this.numPages = numPages;
	}

	public int getNumRecordsPerPage() {
		return numRecordsPerPage;
	}

	public void setNumRecordsPerPage(int numRecordsPerPage) {
		this.numRecordsPerPage = numRecordsPerPage;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public String getPrev() {
		return prev;
	}

	public void setPrev(String prev) {
		this.prev = prev;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public List<T> getEntries() {
		return entries;
	}

	public void setEntries(List<T> entries) {
		this.entries = entries;
	}
}
