package org.carthageking.mc.mcck.core.EXAMPLES.sbtm.cucumber_tests;

/*-
 * #%L
 * mcck-core-EXAMPLES-springboot-thymeleaf
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

import org.carthageking.mc.mcck.core.EXAMPLES.sbtm.TestSpringConfig;
import org.carthageking.mc.mcck.core.EXAMPLES.sbtm.config.CommonConfig;
import org.carthageking.mc.mcck.core.EXAMPLES.sbtm.config.WebMvcConfig;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;

import io.cucumber.junit.platform.engine.Constants;
import io.cucumber.spring.CucumberContextConfiguration;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("cucumber_features/LoginPage.feature")

@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = //
"org.carthageking.mc.mcck.core.cucumber.McckCucumberRandomFilenameJsonFormatter:target/cucumber/cucumber.json")

@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = //
"org.carthageking.mc.mcck.core.EXAMPLES.sbtm.cucumber_tests,"
	+ "org.carthageking.mc.mcck.core.EXAMPLES.sbtm.cucumber_tests.stepdefs")

@CucumberContextConfiguration
@ContextConfiguration(classes = { WebMvcConfig.class, CommonConfig.class, TestSpringConfig.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LoginPageCucumberTest {
}
