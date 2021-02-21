package com.fiedormichal.postrestapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiedormichal.postrestapi.dto.PostDto;
import com.fiedormichal.postrestapi.exception.NoContentException;
import com.fiedormichal.postrestapi.exception.PostNotFoundException;
import com.fiedormichal.postrestapi.mapper.PostDtoMapper;
import com.fiedormichal.postrestapi.model.Post;
import com.fiedormichal.postrestapi.repository.PostRepository;
import com.fiedormichal.postrestapi.service.PostService;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void get_post_by_title_should_return_single_post() throws Exception {
        String title = "Test title";
        final Post post = new Post(1, 2, title, "Body for test");

        when(postRepository.findByTitle(title)).thenReturn(Optional.of(post));

        mockMvc.perform(get("/posts/Test title"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.body", is("Body for test")));
    }


    @Test
    void get_post_by_title_should_respond_not_found_status_when_title_not_found() throws Exception {
        String title = "Test title";
        final Post post = new Post(1, 2, title, "Body for test");

        when(postRepository.findByTitle(title)).thenReturn(Optional.of(post));

        mockMvc.perform(get("/posts/Test"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void get_post_by_title_should_respond_not_found_status_when_method_not_found() throws Exception {
        String title = "Test title";
        final Post post = new Post(1, 2, title, "Body for test");

        when(postRepository.findByTitle(title)).thenReturn(Optional.of(post));

        mockMvc.perform(get("/post/Test title"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void get_all_posts_should_return_all_posts() throws Exception {
        final PostDto post1 = PostDto.builder()
                .id(1)
                .title("Test title1")
                .body("Body for test1")
                .build();
        final PostDto post2 = PostDto.builder()
                .id(2)
                .title("Test title2")
                .body("Body for test2")
                .build();
        List<PostDto> postDtos = Arrays.asList(post1, post2);

        when(postService.findAllPosts()).thenReturn(postDtos);

        mockMvc.perform(get("/posts/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test title1")))
                .andExpect(jsonPath("$[0].body", is("Body for test1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Test title2")))
                .andExpect(jsonPath("$[1].body", is("Body for test2")));
    }

    @Test
    void get_all_posts_should_respond_not_found_status_when_database_is_empty() throws Exception {
        when(postService.findAllPosts()).thenThrow(NoContentException.class);

        mockMvc.perform(get("/posts/"))
                .andDo(print())
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isNotFound());

    }

    @Test
    void get_all_posts_should_respond_not_found_status_when_method_not_found() throws Exception {
        when(postService.findAllPosts()).thenThrow(NoContentException.class);

        mockMvc.perform(get("/post/"))
                .andDo(print())
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isNotFound());

    }

    @Test
    void  edit_post_should_respond_ok_status() throws Exception {
        Post post = new Post(1,2, "test title", "test body");
        String postAsAString = objectMapper.writeValueAsString(post);

        mockMvc.perform(put("/posts/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postAsAString))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void  edit_post_should_respond_bad_request_status_when_Json_is_malformed() throws Exception {
        JSONObject postAsAJson = new JSONObject();
        postAsAJson.put("id", "s");
        postAsAJson.put("title", "test title");
        postAsAJson.put("body", "test body");
        String postAsAString = objectMapper.writeValueAsString(postAsAJson);
        mockMvc.perform(put("/posts/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postAsAString))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void  edit_post_should_respond_bad_request_status_when_id_violation_error_occur() throws Exception {
        JSONObject postAsAJson = new JSONObject();
        postAsAJson.put("id", 0);
        postAsAJson.put("title", "test title");
        postAsAJson.put("body", "test body");
        String postAsAString = objectMapper.writeValueAsString(postAsAJson);
        mockMvc.perform(put("/posts/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postAsAString))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void  edit_post_should_respond_bad_request_status_when_title_violation_error_occur() throws Exception {
        JSONObject postAsAJson = new JSONObject();
        postAsAJson.put("id", 1);
        postAsAJson.put("title", "test");
        postAsAJson.put("body", "test body");
        String postAsAString = objectMapper.writeValueAsString(postAsAJson);
        mockMvc.perform(put("/posts/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postAsAString))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void edit_post_should_respond_bad_request_status_when_body_violation_error_occur() throws Exception {
        JSONObject postAsAJson = new JSONObject();
        postAsAJson.put("id", 1);
        postAsAJson.put("title", "test body");
        postAsAJson.put("body", "test");
        String postAsAString = objectMapper.writeValueAsString(postAsAJson);
        mockMvc.perform(put("/posts/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postAsAString))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void edit_post_should_respond_not_found_status_when_method_not_found() throws Exception {
        JSONObject postAsAJson = new JSONObject();
        postAsAJson.put("id", 1);
        postAsAJson.put("title", "test body");
        postAsAJson.put("body", "test");
        String postAsAString = objectMapper.writeValueAsString(postAsAJson);
        mockMvc.perform(put("/post/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postAsAString))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_post_should_respond_ok_status() throws Exception {
        mockMvc.perform(delete("/posts/1")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void delete_post_should_respond_bad_request_status_when_id_not_a_number() throws Exception {
        mockMvc.perform(delete("/posts/s")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_post_should_respond_not_found_status_when_method_not_found() throws Exception {
        mockMvc.perform(delete("/post/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}