package com.automation.selenium.cucumber.core;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.openqa.selenium.JavascriptExecutor;
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
import org.openqa.selenium.remote.SessionId;

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
			} else {
				caps.setCapability("browserName", "Chrome");
		    	caps.setCapability("version", "73.0");
			}
		} else if (browser != null && browser.startsWith("ie")) {
			InternetExplorerOptions options = new InternetExplorerOptions();
			caps = new DesiredCapabilities(options);
			if (!"false".equals(local)) {
				InternetExplorerDriver ied = new InternetExplorerDriver(options);
				return ied;
			} else {
				String versionNumber = "11.0";
	    		if (!browser.replaceAll("[^0-9.]+", "").isEmpty()) {
	    			versionNumber = browser.replaceAll("[^0-9.]+", "");
	    		}
	    		
	    		caps.setCapability("version", versionNumber);
			}
		} else {
			FirefoxOptions options = new FirefoxOptions();
			caps = new DesiredCapabilities(options);
			if (!"false".equals(local)) {
				FirefoxDriver ffd = new FirefoxDriver(options);
				return ffd;
			} else {
				caps.setCapability("version", "66.0");
			}

		}

		if ("false".equals(local)) {
			try {
				String sauceUser = System.getProperty(Constants.SAUCE_USER);
			    String sauceAccessKey = System.getProperty(Constants.SAUCE_ACCESS_KEY);
			    
			    String hubURL = "http://localhost:4444/wd/hub";
			    
			    boolean isSauce = false;
			    
			    if ((sauceUser != null && !sauceUser.isEmpty()) ||
		    			(sauceAccessKey != null && !sauceAccessKey.isEmpty())) {
			    	isSauce = true;
			    	getScenarioContext().getScenario().write("Running in Sauce");
			    	
			    	hubURL = "http://ondemand.saucelabs.com:80/wd/hub";
			    	
			    	caps.setCapability("username", sauceUser);
			        caps.setCapability("accessKey", sauceAccessKey);

			        caps.setCapability("maxDuration", 3600);
			        caps.setCapability("commandTimeout", 600);
			        caps.setCapability("idleTimeout", 1000);

			    	caps.setCapability("platform", "Windows 10");
			    	
			    	caps.setCapability("build", System.getProperty(Constants.SAUCE_BUILD));
			    	
			    	caps.setCapability("project", "Selenium Automation");
			    	caps.setCapability("name", getScenarioContext().getScenario().getName());
			    	
			    }
				
				RemoteWebDriver driver = new RemoteWebDriver(new URL(
						hubURL), caps);
				driver.setFileDetector(new LocalFileDetector());
				
				if (isSauce) {
		    		SessionId sessionId = driver.getSessionId();
		    		String authKey = sauceUser + ":" + sauceAccessKey;
		    		getScenarioContext().getScenario().write("SessionID: " + sessionId.toString());
		    		
					try {
						SecretKeySpec sk = new SecretKeySpec(authKey.getBytes(), "HMACMD5"); 
			            Mac mac = Mac.getInstance("HmacMD5");
						mac.init(sk);
						byte[] result = mac.doFinal(sessionId.toString().getBytes());
						byte[] hexBytes = new Hex().encode(result); 
				        authKey = new String(hexBytes, "ISO-8859-1"); 
					} catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					getScenarioContext().getScenario().embed(
							("<a href=\"https://saucelabs.com/jobs/" + sessionId.toString() + "?auth=" + authKey + "\">Sauce Run</a>").getBytes(),
							"text/html");
		    	}
				
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
		
		if (System.getProperty(Constants.SAUCE_USER) != null && !System.getProperty(Constants.SAUCE_USER).equals("")) {
			((JavascriptExecutor)getDriver()).executeScript("sauce:job-result=" + (!scenario.isFailed() ? "passed" : "failed"));
	        
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
