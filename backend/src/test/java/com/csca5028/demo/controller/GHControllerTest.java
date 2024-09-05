package com.csca5028.demo.controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * integration test for csca5028
 * this test brings up the spring boot application and tests the rest endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
public class GHControllerTest {
  @Autowired
  MockMvc mockMvc;

  @Test
  void shouldReturnDefaultMessage() throws Exception {

    mockMvc
        .perform(get("/repo")
                .queryParam("username", "admin")
                .queryParam("password", "admin"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("149001365")));
  }

  @Test
  void shouldReturnUnauthorized() throws Exception {

    mockMvc
            .perform(get("/repo")
                    .queryParam("username", "not admin")
                    .queryParam("password", "not admin"))
            .andExpect(status().isUnauthorized());
  }
}
