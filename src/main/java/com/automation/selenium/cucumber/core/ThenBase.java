package com.automation.selenium.cucumber.core;

import org.testng.Assert;

public abstract class ThenBase extends StepBase {
	
	public void assertEquals(String actual, String expected, String message, Boolean ignoreCase) {
		
		if (expected == null) {
			expected = "";
		} else if (ignoreCase) {
			expected = expected.toLowerCase();
		}

		if (actual == null) {
			actual = "";
		} else if (ignoreCase) {
			actual = actual.toLowerCase();
		}
		
		String status = "PASSED";
		String actualMessage = "";
		
    	try {
    		Assert.assertEquals(actual.trim(), expected.trim(), message);
    		actualMessage += "Actual: " + actual + " == Expected: " + expected;
        } catch (AssertionError e) {
        	status = "FAILED";
        	actualMessage += "Actual: " + actual + " != Expected: " + expected;
        	throw new RuntimeException(e);
        } finally {
        	log(message, status, actualMessage);
        }
	}

	public void assertEquals(String actual, String expected, String message) {
		assertEquals(actual, expected, message, false);
	}

	public void assertNotEquals(String one, String two, String message) {
		if (one == null) {
			one = "";
		}

		if (two == null) {
			two = "";
		}
		
		String status = "PASSED";
		String actual = "";
        try {
    		Assert.assertNotEquals(one.trim(), two.trim(), message);
    		actual += "First: " + one + " != Second: " + two;
        } catch (AssertionError e) {
        	status = "FAILED";
        	actual += "First: " + one + " == Second: " + two;
        	throw new RuntimeException(e);
        } finally {
        	log(message, status, actual);
        }
	}

	public void assertNotNull(Object value, String message) {

		String status = "PASSED";
    	try {
    		Assert.assertNotNull(value, message);
        } catch (AssertionError e) {
        	status = "FAILED";
        	throw new RuntimeException(e);
        } finally {
        	log(message, status);
        }
	}

	public void assertNull(Object value, String message) {
		
		String status = "PASSED";
    	try {
    		Assert.assertNull(value, message);
        } catch (AssertionError e) {
        	status = "FAILED";
        	throw new RuntimeException(e);
        } finally {
        	log(message, status);
        }
	}

	public void assertTrue(Boolean value, String message) {

		String status = "PASSED";
    	try {
    		Assert.assertTrue(value, message);
        } catch (AssertionError e) {
        	status = "FAILED";
        	throw new RuntimeException(e);
        } finally {
        	log(message, status);
        }
	}
	
	public void assertFalse(Boolean value, String message) {
		
		String status = "PASSED";
        
    	try {
    		Assert.assertFalse(value, message);
        } catch (AssertionError e) {
        	status = "FAILED";
        	throw new RuntimeException(e);
        } finally {
        	log(message, status);
        }
	}
	
	public void assertContains(String container, String textToMatch, String message) {
		
		assertContains(container, textToMatch, message, false);
	}

	public void assertContains(String container, String textToMatch, String message, Boolean ignoreCase) {

		if (ignoreCase) {
        	container = container.toLowerCase();
        	textToMatch = textToMatch.toLowerCase();
        }
        
        String status = "PASSED";
        String actual = "";
 
    	try {
    		Assert.assertTrue(container.contains(textToMatch), message);
    		actual = "actual text: " + container + " contains text to match: " + textToMatch;
        } catch (AssertionError e) {
        	status = "FAILED";
        	actual = "actual text: " + container + " does not contain text to match: " + textToMatch;
        	throw new RuntimeException(e);
        } finally {
        	log(message, status, actual);
        }   
    }
	
	private void log(String message, String status, String actual) {
		String log = "Validation for " + message + " completed with status of " + status;
		if (actual != null && !actual.isEmpty())
			log += " and actual results: " + actual;
		getScenarioContext().getScenario().write(log);

	}
	
	private void log(String message, String status) {
		log(message, status, null);
	}

}