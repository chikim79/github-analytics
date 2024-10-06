package com.csca5028.demo.service;

import com.csca5028.demo.domain.GHRepo;
import com.csca5028.demo.domain.GHRepoRepository;
import com.csca5028.demo.domain.LLMResponse;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
public class SQSListener {

  ObjectMapper objectMapper = new ObjectMapper();

  final BedrockRuntimeClient bedrockRuntimeClient;
  final S3Client s3Client;
  final GHRepoRepository ghRepoRepository;

  private final static String S3_BUCKET = "gh-analytics-csca5028-build";

  private final static String prompt =
      """
Analyze this build file for respond in json format with following attributes: \\"java_version\\", \\"top_dependency\\", \\"maven_or_gradle\\", \\"build_tool_version\\" return only the json file.  do not return anything else. {{build_file}}""";

  @SqsListener("gh-analytics")
  public void listen(String message) throws IOException, URISyntaxException {
    GHRepo ghRepo = objectMapper.readValue(message, GHRepo.class);

    var key = ghRepo.getFilePath();

    var buildFileAsString = downloadBuildFile(key);

    var escapedPrompt = objectMapper.writeValueAsString(buildFileAsString);
    String substring = escapedPrompt.substring(1, escapedPrompt.length() - 1);
    var response = invokeModel(prompt.replace("{{build_file}}", substring));

    LLMResponse llmResponse = objectMapper.readValue(response, LLMResponse.class);

    ghRepo.setLlmResponse(llmResponse);

    ghRepoRepository.save(ghRepo);
  }

  public String downloadBuildFile(String key) throws URISyntaxException, IOException {
    // Download the build file from S3

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(S3_BUCKET)
            .key(key)
            .build();

    var response = s3Client.getObject(getObjectRequest);
    var buildFileContent = response.readAllBytes();

    return new String(buildFileContent);
  }

  public String invokeModel(String prompt) throws JsonProcessingException {
    var modelId = "anthropic.claude-3-haiku-20240307-v1:0";

    // The InvokeModel API uses the model's native payload.
    // Learn more about the available inference parameters and response fields at:
    // https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-anthropic-claude-messages.html
    var nativeRequestTemplate = """
                {
                    "anthropic_version": "bedrock-2023-05-31",
                    "max_tokens": 512,
                    "temperature": 0.5,
                    "messages": [{
                        "role": "user",
                        "content": "{{prompt}}"
                    }]
                }""";

    // Embed the prompt in the model's native request payload.
    String nativeRequest = nativeRequestTemplate.replace("{{prompt}}", prompt);

    try {
      // Encode and send the request to the Bedrock Runtime.
      var response = bedrockRuntimeClient.invokeModel(request -> request
              .body(SdkBytes.fromUtf8String(nativeRequest))
              .modelId(modelId)
      );

      // Decode the response body.
      var responseBody = objectMapper.readTree(response.body().asUtf8String());

      // Retrieve the generated text from the model's response.
      JsonPointer pointer = JsonPointer.compile("/content/0/text");
      JsonNode textNode = responseBody.at(pointer);

      // Print the retrieved text
      return textNode.asText();

    } catch (SdkClientException e) {
      System.err.printf("ERROR: Can't invoke '%s'. Reason: %s", modelId, e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
