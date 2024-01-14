package org.carthageking.mc.mcck.core.EXAMPLES.sbrb;

import javax.sql.DataSource;

import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao.BookEntityDao;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao.entity.BookEntity;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

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

import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
@EnableJpaRepositories(basePackageClasses = { BookEntityDao.class })
@EntityScan(basePackageClasses = { BookEntity.class })
@EnableJpaAuditing
@EnableEnversRepositories
public class TestDbConfig {

	public TestDbConfig() {
		// noop
	}

	// when running the GenerateDDLForPostgresTest, comment out the
	// bean definition below otherwise Postgres connection will not be
	// recognized in that test
	@Bean
	DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			// we want our tables etc. to be created in a specific schema, not
			// 'public' or the default schema. This is to facilitate separation
			// of concerns. So, when the embedded database is being built, we
			// also execute a script which will contain the commands to create
			// the schema we desire
			.addScript("ddl/00_create_schema.sql")
			// setting below prevents reuse of databases and data from previous
			// tests
			.generateUniqueName(true)
			.build();
	}
}
