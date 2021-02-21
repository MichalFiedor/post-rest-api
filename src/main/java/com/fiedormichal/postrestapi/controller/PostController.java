package com.fiedormichal.postrestapi.controller;

import com.fiedormichal.postrestapi.dto.PostDto;
import com.fiedormichal.postrestapi.model.Post;
import com.fiedormichal.postrestapi.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<Object> getAllPosts(){
        return showListOrSendNoContentStatus();
    }

    @GetMapping("/posts/{userId}")
    public ResponseEntity<List<PostDto>>getPostsByUserId(@PathVariable long userId){
        return ResponseEntity.ok().body(postService.getByUserId(userId));
    }

    @GetMapping("/post/{title}")
    public ResponseEntity<PostDto> getPostByTitle(@PathVariable String title){
        PostDto post = postService.getByTitle(title);
        return ResponseEntity.ok().body(post);
    }

    @PostMapping("/posts/REST")
    public ResponseEntity<String> updatePosts(){
        postService.updatePostsInDataBase();
        return ResponseEntity.ok().body("Post successfully updated");
    }

    @PutMapping("/posts")
    public ResponseEntity<PostDto> editPost(@Valid @RequestBody Post post){
        PostDto editedPost = postService.edit(post);
        return ResponseEntity.ok().body(editedPost);
    }

    @DeleteMapping("posts/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable long postId){
        postService.delete(postId);
        return ResponseEntity.ok().body("Post successfully deleted");
    }

    private ResponseEntity<Object> showListOrSendNoContentStatus(){
        List<PostDto> posts= postService.findAll();
        if(posts.size()==0){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("List is empty.");
        }
        return ResponseEntity.ok().body(posts);
    }
}
