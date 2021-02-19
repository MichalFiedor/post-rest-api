package com.fiedormichal.postrestapi.controller;

import com.fiedormichal.postrestapi.model.Post;
import com.fiedormichal.postrestapi.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/REST")
    @ResponseStatus(value = HttpStatus.OK)
    public void updateAndShowAllPosts(){
        postService.updatedPosts();
    }

    @GetMapping("/posts")
    public List<Post> getAllPosts(){
        return postService.findAll();
    }
}
