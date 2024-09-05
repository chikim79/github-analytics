package com.csca5028.demo.controller;

import com.csca5028.demo.domain.GHRepo;
import com.csca5028.demo.service.GithubJsonParser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class GHController {

  private final File testGHFile;
  private final GithubJsonParser githubJsonParser;

  @GetMapping("/repo")
  public GHRepo getRepo(@Param("username") String username, @Param("password") String password) throws IllegalAccessException, IOException {
    if (username.equals("admin") && password.equals("admin")) {

      return githubJsonParser.parseGHRepo(FileUtils.readFileToString(testGHFile, "UTF-8"));

    } else {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
    }
  }
}
