package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.cucumber_tests.ctx001;

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
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;

import io.cucumber.junit.platform.engine.Constants;
import io.cucumber.spring.CucumberContextConfiguration;

@Suite
@IncludeEngines("cucumber")
// below illustrates loading of one feature file for execution. To include all
// feature files inside the given classpath, just specify the classpath and
// not an individual feature file
@SelectClasspathResource("cucumber_features/ActuatorEndpointsTest.feature")

@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = //
"org.carthageking.mc.mcck.core.cucumber.McckCucumberRandomFilenameJsonFormatter:target/cucumber/cucumber.json,"
	// https://allurereport.org/docs/cucumberjvm/
	// https://allurereport.org/docs/junit5/
	// we enable the plugin for allure for the given version of cucumber 
	+ "org.carthageking.mc.mcck.core.allure.cucumber7.jvm.McckAllureCucumber7Jvm")

// the below annotation is not required to be present, but leaving it out
// will generate some System.err messages about glue code as Cucumber attempts
// to load then using different listeners (Junit3, JUnit4, etc.). To avoid the
// messages, explicitly specify where our glue code (i.e. step definitions) is
//
// additionally, we specify two classpaths. the first one is the classpath where this class file resides
// and the other one is where our step definitions are. doing it this way would allow us to specify
// different context configurations for different tests
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = //
"org.carthageking.mc.mcck.core.EXAMPLES.sbrb.cucumber_tests.ctx001,"
	+ "org.carthageking.mc.mcck.core.EXAMPLES.sbrb.cucumber_tests.stepdefs")

@CucumberContextConfiguration
@ContextConfiguration(classes = { TestSpringConfig.class, CommonConfig.class, TestDbConfig.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// need the annotation below so things like /actuator/prometheus is
// configured properly in test context. the below annotation is not needed
// in the actual application
@AutoConfigureObservability
public class ActuatorEndpointsCucumberTest {
	// This class needs to be empty (i.e. will not contain any @Test methods)
	// otherwise the annotated test methods will be executed normally, which we
	// do not want
}
