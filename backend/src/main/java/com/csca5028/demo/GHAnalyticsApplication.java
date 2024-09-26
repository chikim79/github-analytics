package com.csca5028.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.s3.S3Client;

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

	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
						.credentialsProvider(DefaultCredentialsProvider.create())
						.region(Region.US_EAST_1)
						.build();
	}

	@Bean
	public BedrockRuntimeClient bedrockRuntimeClient() {
		return BedrockRuntimeClient.builder()
						.credentialsProvider(DefaultCredentialsProvider.create())
						.region(Region.US_EAST_1)
						.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(GHAnalyticsApplication.class, args);
	}

}
