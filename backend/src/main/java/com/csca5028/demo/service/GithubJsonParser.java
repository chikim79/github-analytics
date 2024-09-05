package com.csca5028.demo.service;

import com.csca5028.demo.domain.GHRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GithubJsonParser {

  private final ObjectMapper objectMapper;

  public GHRepo parseGHRepo(String json) {
    try {
      return objectMapper.readValue(json, GHRepo.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
