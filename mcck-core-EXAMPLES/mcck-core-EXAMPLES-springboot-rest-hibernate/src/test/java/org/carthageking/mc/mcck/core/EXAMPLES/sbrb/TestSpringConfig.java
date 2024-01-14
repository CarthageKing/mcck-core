package org.carthageking.mc.mcck.core.EXAMPLES.sbrb;

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

import java.net.http.HttpClient;

import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.controller.BooksController;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao.BookEntitySearchDao;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.exception.AppCustomExceptionHandler;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.service.BookService;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.java11.httpclient.Java11HttpClientHttpClientHelper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { BooksController.class, BookService.class, BookEntitySearchDao.class, AppCustomExceptionHandler.class })
public class TestSpringConfig {

	public TestSpringConfig() {
		// noop
	}

	@Bean
	HttpClient httpClient() {
		return HttpClient.newHttpClient();
	}

	@Bean
	HttpClientHelper httpClientHelper(HttpClient httpClient) {
		return new Java11HttpClientHttpClientHelper(httpClient);
	}
}
