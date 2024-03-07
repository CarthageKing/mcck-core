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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@ContextConfiguration(classes = { TestSpringConfig.class, CommonConfig.class, TestDbConfig.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
	// below properties don't need to be set since our default embedded
	// db implementation used is H2. But if the default is different and we
	// want to generate the ddl for H2, then the below properties need to
	// be set
	//"spring.datasource.url=jdbc:h2:mem:testdb",
	//"spring.datasource.driver-class-name=org.h2.Driver",
	//"spring.datasource.username=sa",
	//"spring.datasource.password=",

	// https://thorben-janssen.com/standardized-schema-generation-data-loading-jpa-2-1/
	//
	// Options used to tell Spring and Hibernate on how to perform the
	// DDL generation. Instead of using javax, use jakarta. However, if using
	// older than Java 17 or using libraries that have not been ported to
	// jakarta, then just use the javax package. The options below will create
	// a create.sql file in the target folder
	"spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create",
	"spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=target/create.sql",
})
@McckAllureParentSuite("Unit Tests Suite")
// below annotation will be used to override the allure suite name
@DisplayName("DDL Generation")
// for demo purposes, we disable the annotation below, and specify the sub suite name inside the test method itself
//@McckAllureSubSuite("H2")
class GenerateDDLForH2Test {

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
	// below annotation value will be used to override the allure name of the method
	@DisplayName("H2 method")
	void test_generateDdl() {
		// this is another way of specifying the sub suite. also refer to the link for additional examples
		// https://allurereport.org/docs/junit5/#organize-tests
		//Allure.label(ResultsUtils.SUB_SUITE_LABEL_NAME, "H2");

		// do nothing. add the below to get rid of warning saying that this
		// test method does not have any assertions
		Assertions.assertTrue(true);
	}
}
