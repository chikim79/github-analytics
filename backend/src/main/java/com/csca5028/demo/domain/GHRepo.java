package com.csca5028.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Entity
public class GHRepo {
  @Id
  private Long id;
  private String full_name;
  private String html_url;
  private String description;
  private Long forks_count;
  private Long stargazers_count;
  private Long watchers_count;
  private String builder;

  @Transient
  private String filePath;

  @Embedded
  private LLMResponse llmResponse;
}
