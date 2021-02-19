package com.fiedormichal.postrestapi.dto;

import com.fiedormichal.postrestapi.model.Post;

import java.util.List;
import java.util.stream.Collectors;

public class PostDtoMapper {

    private PostDtoMapper(){

    }

    public static List<PostDto> mapToPostDtos(List<Post> posts){
        return posts.stream()
                .map(post-> mapToPostDto(post))
                .collect(Collectors.toList());
    }

    public static PostDto mapToPostDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .build();
    }
}
