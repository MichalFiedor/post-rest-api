package com.fiedormichal.postrestapi.controller;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fiedormichal.exception.PostNotFoundException;
import com.fiedormichal.postrestapi.dto.PostDto;
import com.fiedormichal.postrestapi.model.Post;
import com.fiedormichal.postrestapi.service.PostService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.minidev.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.data.rest.core.config.JsonSchemaFormat;
import org.springframework.data.rest.webmvc.json.JsonSchema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
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
    public void updatePosts(){
        postService.updatePostsInDataBase();
    }


    @PutMapping("/posts")
    public PostDto editPost(@Valid @RequestBody Post post, BindingResult result){
        checkIfEditedPostHasErrors(result);
        try {
            return postService.edit(post);
        } catch (PostNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with this ID does not exist.");
        }
    }

    @DeleteMapping("posts/{postId}")
    @ResponseStatus(value=HttpStatus.OK)
    public void deletePost(@PathVariable long postId){
        delete(postId);
    }

    private void delete(long postId){
        try {
            postService.delete(postId);
        } catch (PostNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with this ID does not exist.");
        }
    }

    private void checkIfEditedPostHasErrors(BindingResult result){
        if(result.hasErrors()){
            List<ObjectError> allErrors = result.getAllErrors();
            StringBuilder stringBuilder = new StringBuilder();
            for(ObjectError error : allErrors){
                stringBuilder.append(error.getDefaultMessage());
                stringBuilder.append( "\n ");
            }
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, stringBuilder.toString());
        }
    }
}
