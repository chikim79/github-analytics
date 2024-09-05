package com.csca5028.demo.service;

import com.csca5028.demo.domain.GHRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GithubJsonParserTest {

  private GithubJsonParser githubJsonParser = new GithubJsonParser(new ObjectMapper());

  @Test
  void parseGHRepo() throws IOException {
    String resourceName = "test-github.json";

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource(resourceName).getFile());

    String data = FileUtils.readFileToString(file, "UTF-8");

    GHRepo ghRepo = githubJsonParser.parseGHRepo(data);

    assertNotNull(ghRepo);
    assertEquals(149001365, ghRepo.getId());
  }
}
