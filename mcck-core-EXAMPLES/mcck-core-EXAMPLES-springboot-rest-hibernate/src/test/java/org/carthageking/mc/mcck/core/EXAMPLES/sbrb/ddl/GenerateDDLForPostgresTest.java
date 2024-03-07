package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.ddl;

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

import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.TestDbConfig;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.TestSpringConfig;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.config.CommonConfig;
import org.carthageking.mc.mcck.core.allure.McckAllureParentSuite;
import org.carthageking.mc.mcck.core.allure.McckAllureSubSuite;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

// see comments in GenerateDDLForH2Test.java to get an idea of these classes
@ContextConfiguration(classes = { TestSpringConfig.class, CommonConfig.class, TestDbConfig.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
	"spring.datasource.url=jdbc:postgresql://localhost:5432/test_db?currentSchema=my_schema",
	"spring.datasource.driver-class-name=org.postgresql.Driver",
	"spring.datasource.username=postgres",
	"spring.datasource.password=root123@",

	// need the below to specify the version of Postgres to use
	"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect",

	"spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create",
	"spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=target/create.sql",
})
// this test class is disabled by default since it doesn't work properly
// when there is an explicit DataSource bean definition in TestDbConfig.java.
// see notes there before removing the below annotation
@Disabled
@McckAllureParentSuite("Unit Tests Suite")
// below annotation will be used to override the allure suite name
@DisplayName("DDL Generation")
@McckAllureSubSuite("Postgres")
class GenerateDDLForPostgresTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test_generateDdl() {
		Assertions.assertTrue(true);
	}
}
