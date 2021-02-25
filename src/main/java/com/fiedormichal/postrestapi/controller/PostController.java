package com.fiedormichal.postrestapi.controller;

import com.fiedormichal.postrestapi.dto.PostDto;
import com.fiedormichal.postrestapi.model.Post;
import com.fiedormichal.postrestapi.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<Object> getAllPosts(){
        List<PostDto> posts= postService.findAllPosts();
        return ResponseEntity.ok().body(posts);
    }

    @GetMapping("/posts/")
    public ResponseEntity<PostDto> getPostByTitle(@RequestParam String title){
        PostDto post = postService.getPostByTitle(title);
        return ResponseEntity.ok().body(post);
    }

    @PostMapping("/posts/REST")
    public ResponseEntity<String> updatePosts() throws IOException {
        postService.updatePostsInDataBase();
        return ResponseEntity.ok().body("Posts successfully updated");
    }

    @PutMapping("/posts")
    public ResponseEntity<PostDto> editPost(@Valid @RequestBody Post post){
        PostDto editedPost = postService.editPost(post);
        return ResponseEntity.ok().body(editedPost);
    }

    @DeleteMapping("posts/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable long postId){
        postService.deletePost(postId);
        return ResponseEntity.ok().body("Post successfully deleted");
    }
}
