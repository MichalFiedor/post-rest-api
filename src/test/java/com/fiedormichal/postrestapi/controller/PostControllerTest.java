package com.fiedormichal.postrestapi.controller;

import com.fiedormichal.postrestapi.dto.PostDto;
import com.fiedormichal.postrestapi.model.Post;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_get_single_post() throws Exception {
        //given
        final long postId = 1;
//        final Post post = new Post(postId, 2, "Test title", "Body for test", false);
        final PostDto postDto = PostDto.builder()
                .id(postId)
                .title("Test title")
                .body("Body for test")
                .build();
        //when
        mockMvc.perform(get("/posts/1"))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().is(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is("1")));
        //then
    }



}