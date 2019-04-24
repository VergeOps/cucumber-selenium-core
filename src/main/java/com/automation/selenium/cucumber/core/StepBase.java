package com.automation.selenium.cucumber.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.automation.selenium.cucumber.core.utility.Constants;
import com.automation.selenium.cucumber.core.utility.ScenarioContext;

import cucumber.api.Scenario;

/*
 * This is class level documentation
 */
public class StepBase {

	private static ThreadLocal<ScenarioContext> activeContexts;
	
	protected String project;
	protected String environment;
	
	private Random rand;
	
	public Random getRandom() {
		if (rand == null)
			rand = new Random();

		return rand;
	}
	
	protected WebDriver getDriver() {

		if (getScenarioContext().getDriver() == null) {			
			getScenarioContext().setDriver(createDriver());
			getDriver().manage().timeouts()
			.implicitlyWait(Constants.WEBDRIVER_TIMEOUT, TimeUnit.SECONDS);
			getDriver().manage().window().maximize();
		}

		return getScenarioContext().getDriver();
	}
	
	private static WebDriver createDriver() {
		String browser = System.getProperty("browser.type");
		String local = System.getProperty("run.local");	
		
		DesiredCapabilities caps = null;
		
		if ("chrome".equals(browser)) {
			ChromeOptions options = new ChromeOptions();
			caps = new DesiredCapabilities(options);
			if (!"false".equals(local)) {
				ChromeDriver cd = new ChromeDriver(options);
				return cd;
			}
		} else if (browser != null && browser.startsWith("ie")) {
			InternetExplorerOptions options = new InternetExplorerOptions();
			caps = new DesiredCapabilities(options);
			if (!"false".equals(local)) {
				InternetExplorerDriver ied = new InternetExplorerDriver(options);
				return ied;
			}
		} else {
			FirefoxOptions options = new FirefoxOptions();
			caps = new DesiredCapabilities(options);
			if (!"false".equals(local)) {
				FirefoxDriver ffd = new FirefoxDriver(options);
				return ffd;
			} 

		}

		if ("false".equals(local)) {
			try {
				RemoteWebDriver driver = new RemoteWebDriver(new URL(
						"http://{GRID_IP}:4444/wd/hub"), caps);
				driver.setFileDetector(new LocalFileDetector());
				return driver;
			} catch (MalformedURLException e) {

				e.printStackTrace();
			}

		}
		
		return null;
	}
	

	public void wait(int seconds) {

		try {
			Thread.sleep(seconds);
		} catch (Exception e) {

		}
	}

	protected void initializeWebDriver(Scenario scenario) {
		getScenarioContext().setScenario(scenario);
		getDriver();
	}

	protected void killWebDriver(Scenario scenario) {	
		
		if (scenario.isFailed()) {
         scenario.embed(
        		 ((TakesScreenshot)getDriver())
        		 .getScreenshotAs(OutputType.BYTES), "image/png");
        }
		
		getScenarioContext().getDriver().close();
		getScenarioContext().getDriver().quit();
		getScenarioContext().setDriver(null);
		getScenarioContext().clearContextData();
		getScenarioContext().setScenario(null);
	}
	
	protected static ScenarioContext getScenarioContext() {

		if (activeContexts == null) {
			activeContexts = new ThreadLocal<ScenarioContext>();
		}
		
		if (activeContexts.get() == null) {
			ScenarioContext context = new ScenarioContext();
			activeContexts.set(context);
		}
		
		return activeContexts.get();
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T getData(Class<T> clazz) {
		if (!getScenarioContext().isInContext(clazz.getName())) {
			try {
				getScenarioContext().setContextData(clazz.getName(), clazz.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return (T) getScenarioContext().getContextData(clazz.getName());
	}
	
	protected <T extends FormData>void setDataObject(T data) {
		getScenarioContext().setContextData(data.getClass().getName(), data);
	}

}
