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
  private String node_id;
  private String full_name;
  private String html_url;
  private String description;
  private String stargazers_count;
  private String watchers_count;

  @Transient
  private String build_file_s3_loc;

  @Embedded
  private LLMResponse llmResponse;
}
