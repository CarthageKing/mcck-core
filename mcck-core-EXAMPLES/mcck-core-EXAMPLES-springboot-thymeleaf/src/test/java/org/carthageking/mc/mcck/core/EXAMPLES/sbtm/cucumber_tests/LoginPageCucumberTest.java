package org.carthageking.mc.mcck.core.EXAMPLES.sbtm.cucumber_tests;

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
