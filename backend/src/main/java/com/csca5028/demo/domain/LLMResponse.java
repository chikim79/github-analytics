package com.csca5028.demo.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Embeddable
public class LLMResponse {
  private String java_version;
  private String top_dependency;
  private String maven_or_gradle;
  private String build_tool_version;
}
