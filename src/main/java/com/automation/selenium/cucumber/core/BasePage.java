package com.automation.selenium.cucumber.core;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.automation.selenium.cucumber.core.utility.Constants;

public abstract class BasePage {

	protected WebDriver getDriver() {
		return driver;
	}

	private WebDriver driver;

	private WebDriverWait wait;

	protected BasePage(WebDriver driver) {

		PageFactory.initElements(driver, this);
		wait = new WebDriverWait(driver, Constants.WEBDRIVER_TIMEOUT);
		this.driver = driver;

	}

	protected void populateTextField(WebElement element , String data) {
		if ( data != null) {
			clear(element);		
			enterText(element,data);
		}
		
		
	}
	
	protected void populateSelectFieldByText(WebElement element , String data) {
		if ( data != null) {		
			selectByText(element,data);		
		}
		
	}
	
	protected void populateSelectFieldByValue(WebElement element , String data) {
		if ( data != null) {
			selectByValue(element,data);
		}
		
	}
	
	protected String getText(WebElement element) {
		waitForClickable(element);
		log("Getting text for", element);
		return element.getText();
	}
	
	protected void click(WebElement element) {
		waitForClickable(element);
		log("Clicking on", element);
		element.click();
		
	}
	
	protected void selectBox(WebElement element) {
		waitForClickable(element);
		if(!element.isSelected()) {
			log("Selecting", element);
			element.click();
		}
		
	}
	
	protected void unSelectBox(WebElement element) {
		waitForClickable(element);
		if(element.isSelected()) {
			log("Deselecting", element);
			element.click();
		}
		
	}
	
	protected void clear(WebElement element ) {
		waitForClickable(element);
		log("Clearing", element);
		element.clear();
	}
	
	private void enterText(WebElement element , String data) {
		waitForClickable(element);
		log("Entering " + data + " into", element);
		element.sendKeys(data.trim());
	}
	
	private void selectByText(WebElement element , String data) {
		waitForClickable(element);
		log("Selecting " + data + " by visible text for", element);
		Select item = new Select(element);
		item.selectByVisibleText(data);
	}
	
	private void selectByValue(WebElement element , String data) {
		waitForClickable(element);
		log("Selecting " + data + " by value for", element);
		Select item = new Select(element);
		item.selectByValue(data);
	}

	protected void executeJavaScript(String scriptToExecute) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(scriptToExecute);
	}

	protected void specialClick(WebElement element) {
		waitForClickable(element);
		log("Clicking on", element);
		Actions actions = new Actions(driver);
		actions.moveToElement(element);
		actions.click().build().perform();
	}

	protected void hover(WebElement element) {
		waitForClickable(element);
		log("Hovering over", element);
		Actions actions = new Actions(driver);
		actions.moveToElement(element);
		actions.build().perform();
	}

	protected void changeWait(int waitTime) {
		driver.manage().timeouts().implicitlyWait(waitTime, TimeUnit.SECONDS);
	}

	protected void resetWait() {
		driver.manage().timeouts()
				.implicitlyWait(Constants.WEBDRIVER_TIMEOUT, TimeUnit.SECONDS);
	}

	protected void waitForVisible(WebElement element) {
		wait.until(ExpectedConditions.visibilityOf(element));
	}
	
	protected void waitForText(WebElement element, String text) {
		wait.until(ExpectedConditions.textToBePresentInElement(element, text));
	}

	protected void waitForClickable(WebElement element) {
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}
	
	protected void waitForNotVisible(WebElement element) {
		wait.until(ExpectedConditions.invisibilityOf(element));
	}

	protected void acceptAlert() {

		try {
			Alert alert = driver.switchTo().alert();
			log("Accepting alert");
			alert.accept();

		} catch (Exception e) {
			try {
				Alert alert = driver.switchTo().alert();
				log("Accepting alert");
				alert.accept();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private void log(String message, WebElement element) {
		String name = getLogicalName(element);
		StepBase.getScenarioContext().getScenario().write(message + " " + name);
	}
	
	private void log(String message) {
		StepBase.getScenarioContext().getScenario().write(message);
	}
	
	private String getLogicalName(WebElement element) {
		String name = "";
		if ((element.getTagName().equals("input") && 
				(element.getAttribute("type").contains("text") || element.getAttribute("type").contains("password"))) || 
				element.getTagName().equals("select")) {
			name = element.getAttribute("name");
			if (name == null || name.isEmpty()) {
				name = element.getAttribute("id");
			}
		} else if (element.getTagName().equals("input") && 
				(element.getAttribute("type").contains("radio") || element.getAttribute("type").contains("checkbox"))) {
			name = element.getAttribute("name") + " : " + element.getAttribute("id");
		} else if (element.getText() != null) {
			name = element.getText();
			if (name == null || name.isEmpty()) {
				name = element.getAttribute("id");
			}
			if (name == null || name.isEmpty()) {
				name = element.getAttribute("name");
			}
		}
		
		if (name == null) {
			name = "Unknown Element";
		}
		
		return name;
	}
	
	protected void sleep(int seconds) {

		try {
			Thread.sleep(seconds * 1000);
		} catch (Exception e) {

		}
	}
}