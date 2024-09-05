package com.csca5028.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}



	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
