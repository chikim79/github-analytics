package com.csca5028.demo.controller;

import com.csca5028.demo.domain.GHRepo;
import com.csca5028.demo.domain.GHRepoRepository;
import com.csca5028.demo.service.GithubJsonParser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class GHController {

  private final File testGHFile;
  private final GithubJsonParser githubJsonParser;
  private final GHRepoRepository ghRepoRepository;

  @GetMapping("/repo")
  public GHRepo getRepo(@Param("username") String username, @Param("password") String password) throws IllegalAccessException, IOException {
    if (username.equals("admin") && password.equals("admin")) {

      return githubJsonParser.parseGHRepo(FileUtils.readFileToString(testGHFile, "UTF-8"));

    } else {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
    }
  }

  @PostMapping("/repo")
  public void createRepo(@RequestBody GHRepo repo) {
    ghRepoRepository.save(repo);
  }

  @GetMapping("/repo/{id}")
  public GHRepo getRepoById(@PathVariable("id") Long id) {
    return ghRepoRepository.findById(id).get();
  }

  @GetMapping("/repos/all")
  public Iterable<GHRepo> getAllRepos() {
    return ghRepoRepository.findAll();
  }

}
