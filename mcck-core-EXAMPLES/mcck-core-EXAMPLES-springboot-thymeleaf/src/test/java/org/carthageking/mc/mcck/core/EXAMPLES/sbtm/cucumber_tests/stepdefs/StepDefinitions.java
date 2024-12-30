package org.carthageking.mc.mcck.core.EXAMPLES.sbtm.cucumber_tests.stepdefs;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class StepDefinitions {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StepDefinitions.class);

	@LocalServerPort
	private int port;

	private String baseUrl;

	private WebDriver webDriver;

	@Before
	public void beforeBeginScenario() {
		baseUrl = "http://localhost:" + port;
		LOG.trace("Initializing Cucumber scenario context");

		// =============
		// firefox
		// =============
		FirefoxOptions ffo = new FirefoxOptions();
		// add below for headless mode (i.e. don't show browser window)
		//ffo.addArguments("--headless");
		webDriver = new FirefoxDriver(ffo);

		webDriver.manage().deleteAllCookies();
		webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		webDriver.manage().window().maximize();
	}

	@After
	public void afterEndScenario() {
		LOG.trace("Destroying Cucumber scenario context");

		webDriver.close();
	}

	@BeforeStep
	public void beforeStep(Scenario scenario) {
		// delay added primarily to ensure WebDriver waits for page to load
		// successfully, and to make the output easier to follow
		awaitFor(Duration.ofMillis(300));
	}

	@When("I navigate to {string} page")
	public void whenINavigateToPageContextPath(String pageContextPath) {
		webDriver.get(baseUrl + pageContextPath);
	}

	@When("I enter on element {string} with value {string}")
	public void whenIEnterOnElementWithValue(String elementId, String theValue) {
		WebElement elem = webDriver.findElement(By.id(elementId));
		Assertions.assertNotNull(elem);
		elem.clear();
		if ("BLANK".equals(theValue)) {
			return;
		}
		elem.sendKeys(theValue);
	}

	@When("I press {string} key on element {string}")
	public void whenIPressKeyOnElement(String theKey, String elementId) {
		WebElement elem = webDriver.findElement(By.id(elementId));
		Assertions.assertNotNull(elem);
		elem.sendKeys(Keys.valueOf(theKey));
	}

	@When("I click on element {string}")
	public void whenIClickOnElement(String elementId) {
		WebElement elem = webDriver.findElement(By.id(elementId));
		Assertions.assertNotNull(elem);
		elem.click();
	}

	@When("I wait {int} second\\(s) after waiting for {int} second\\(s) for element {string} to appear")
	public void whenIWaitAfterWaitingForElementToAppear(int idleWaitSeconds, int elemAppearWaitTimeoutSeconds, String elementId) {
		Duration oldDuration = webDriver.manage().timeouts().getImplicitWaitTimeout();
		try {
			webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(elemAppearWaitTimeoutSeconds));
			WebElement elem = webDriver.findElement(By.id(elementId));
			Assertions.assertNotNull(elem);
		} finally {
			webDriver.manage().timeouts().implicitlyWait(oldDuration);
		}

		awaitFor(Duration.ofSeconds(idleWaitSeconds));
	}

	@When("I wait {int} second\\(s) after waiting for {int} second\\(s) for element class {string} to appear")
	public void whenIWaitAfterWaitingForElementClassToAppear(int idleWaitSeconds, int elemAppearWaitTimeoutSeconds, String elemClass) {
		Duration oldDuration = webDriver.manage().timeouts().getImplicitWaitTimeout();
		try {
			webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(elemAppearWaitTimeoutSeconds));
			List<WebElement> lst = webDriver.findElements(By.className(elemClass));
			Assertions.assertEquals(true, !lst.isEmpty(), "lst size is empty");
		} finally {
			webDriver.manage().timeouts().implicitlyWait(oldDuration);
		}

		awaitFor(Duration.ofSeconds(idleWaitSeconds));
	}

	@Then("I am redirected to {string} page")
	public void thenIAmRedirectedToPageContextPath(String pageContextPath) throws MalformedURLException {
		URL url = new URL(webDriver.getCurrentUrl());
		Assertions.assertEquals(true, url.getPath().startsWith(pageContextPath));
	}

	@Then("The text of element {string} ends with {string}")
	public void thenTheTextOfElementEndsWithString(String elementId, String theValue) {
		WebElement elem = webDriver.findElement(By.id(elementId));
		Assertions.assertEquals(true, elem.getText().endsWith(theValue));
	}

	@Then("The element class {string} contains text {string}")
	public void thenTheElementClassContainsText(String elemClass, String theValue) {
		List<WebElement> lst = webDriver.findElements(By.className(elemClass));
		Assertions.assertEquals(true, !lst.isEmpty(), "lst size is empty");
		for (WebElement elem : lst) {
			if (elem.getText().contains(theValue)) {
				return;
			}
		}
		Assertions.fail("did not find any element with class \"" + elemClass + "\" which contains the text: " + theValue);
	}

	private void awaitFor(Duration duration) {
		Awaitility.await().pollDelay(duration).until(() -> true);
	}
}
