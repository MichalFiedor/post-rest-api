package com.fiedormichal.postrestapi.mapper;

import com.fiedormichal.postrestapi.model.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class JsonPostMapperTest {

    @Autowired
    private JsonPostMapper jsonPostMapper;

    @Test
    void should_return_posts_list_when_given_correct_api_url() throws IOException {
        //given
        final String apiURL = "https://jsonplaceholder.typicode.com/posts";
        //when
        List<Post>result = jsonPostMapper.getMappedPostsList(apiURL);
        //then
        assertNotNull(result);
        assertEquals(Post.class, result.get(0).getClass());
    }

    @Test
    void should_throw_IOException_when_given_incorrect_api_url() {
        //given
        final String apiURL = "https://jsonplaceholder.typicode.com/post";
        //when
        assertThrows(IOException.class, ()->jsonPostMapper.getMappedPostsList(apiURL));
    }
}