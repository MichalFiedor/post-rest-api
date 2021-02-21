package com.fiedormichal.postrestapi.mapper;

import com.fiedormichal.postrestapi.dto.PostDto;
import com.fiedormichal.postrestapi.model.Post;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostDtoMapperTest {

    @Test
    void should_return_postDto_object(){
        //given
        final Post post = new Post(2,1, "title", "body");
        //when
        final PostDto result = PostDtoMapper.mapToPostDto(post);
        //then
        assertEquals(PostDto.class, result.getClass());
        assertEquals(2, result.getId());
        assertEquals("title", result.getTitle());
        assertEquals("body", result.getBody());
    }

    @Test
    void should_return_postDtos_list(){
        //given
        final Post post1 = new Post(2,1, "title1", "body1");
        final Post post2 = new Post(3,2, "title2", "body2");
        final List<Post> posts = new ArrayList<>();
        posts.add(post1);
        posts.add(post2);
        //when
        final List<PostDto> result = PostDtoMapper.mapToPostDtos(posts);
        //then
        assertEquals(2, result.size());
        assertEquals("title2", result.get(1).getTitle());
        assertEquals("body1", result.get(0).getBody());
        assertEquals(2, result.get(0).getId());
    }

}