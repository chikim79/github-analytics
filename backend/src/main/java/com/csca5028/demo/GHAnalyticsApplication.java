package com.csca5028.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
public class GHAnalyticsApplication {

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public File testGHFile() {
		return new File("src/main/resources/test-github.json");
	}



	public static void main(String[] args) {
		SpringApplication.run(GHAnalyticsApplication.class, args);
	}

}
