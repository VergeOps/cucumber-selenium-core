package com.automation.selenium.cucumber.core;

import java.time.LocalDateTime;

import com.github.javafaker.Faker;

public class FormData {
	
	protected LocalDateTime now;
	protected Faker faker;
	
	public FormData() {
		now = LocalDateTime.now();
		faker = new Faker();
		
	}

}
