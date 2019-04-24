package com.automation.selenium.cucumber.core.utility;

import java.util.HashMap;
import java.util.Set;

import org.openqa.selenium.WebDriver;

import cucumber.api.Scenario;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.ObjectWriter;

public class ScenarioContext {

	private WebDriver driver;
	private Scenario scenario;
	
	private HashMap<String, Object> contextData;

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}
	
	public boolean isInContext(String item) {
		if (contextData == null) {
			contextData = new HashMap<String, Object>();
		}
		return contextData.containsKey(item);
	}

	public Object getContextData(String item) {
		if (contextData == null) {
			contextData = new HashMap<String, Object>();
		}
		return contextData.get(item);
	}

	public void setContextData(String item, Object value) {
		if (contextData == null) {
			contextData = new HashMap<String, Object>();
		}
		
		if (contextData.containsKey(item)) {
			logKey(item);
		}
		
		contextData.put(item, value);
	}
	
	public void clearContextData() {
		logContextData();
		contextData = null;
	}
	
	private void logContextData() {
		if (contextData != null) {
			Set<String> keys = contextData.keySet();
			for (String key : keys) {
				logKey(key);
			}
		}
	}
	
	private void logKey(String item) {
		if (contextData != null) {
			Object obj = contextData.get(item);
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = "";
			try {
				json = ow.writeValueAsString(obj);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			scenario.write("Context Data " + item + " of type " + obj.getClass() + ": " + json);
		}
	}

}
