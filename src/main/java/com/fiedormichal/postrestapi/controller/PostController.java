package com.fiedormichal.postrestapi.controller;

import com.fiedormichal.postrestapi.dto.PostDto;
import com.fiedormichal.postrestapi.model.Post;
import com.fiedormichal.postrestapi.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/posts")
    public List<PostDto> getAllPosts(){
        return postService.findAll();
    }

    @GetMapping("/posts/{userId}")
    public List<PostDto> getPostsByUserId(@PathVariable long userId){
        return postService.getByUserId(userId);
    }

    @GetMapping("/post/{title}")
    public PostDto getPostByTitle(@PathVariable String title){
        return postService.getByTitle(title);
    }

    @PostMapping("/posts/REST")
    @ResponseStatus(value = HttpStatus.OK)
    public void updateAndShowAllPosts(){
        postService.updatedPosts();
    }

    @PutMapping("/posts")
    public PostDto editPost(@RequestBody Post post){
        try {
            return postService.edit(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @DeleteMapping("posts/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deletePost(@PathVariable long postId){
        delete(postId);
    }

    private void delete(long postId){
        try {
            postService.delete(postId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
