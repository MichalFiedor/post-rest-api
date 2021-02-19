package com.fiedormichal.postrestapi.service;

import com.fiedormichal.postrestapi.dto.PostDto;
import com.fiedormichal.postrestapi.dto.PostDtoMapper;
import com.fiedormichal.postrestapi.mapper.JsonPostMapper;
import com.fiedormichal.postrestapi.model.Post;
import com.fiedormichal.postrestapi.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final JsonPostMapper jsonPostMapper;

    public void updatedPosts() {
        String apiUrl = "https://jsonplaceholder.typicode.com/posts";
        List<Post> posts = null;
        try {
            posts = jsonPostMapper.getMappedPostsList(apiUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveAll(posts);
    }

    public void saveAll(List<Post> posts){
        posts.stream().forEach(post -> postRepository.save(post));
    }

    public Post save(Post post){
        return postRepository.save(post);
    }

    public PostDto edit(Post post) throws Exception {
        if(exist(post)){
            post.setUpdated(true);
            postRepository.save(post);
            return PostDtoMapper.mapToPostDto(post);
        } else {
            throw new Exception("Post does not exist.");
        }
    }

    public List<PostDto> findAll(){
        return PostDtoMapper.mapToPostDtos(postRepository.findAll());
    }

    public void delete(long postId) throws Exception {
        Post post = postRepository.findById(postId).orElseThrow(()->new Exception("Post not found"));
        post.setDeleted(true);
        postRepository.save(post);
    }

    public List<PostDto> getByUserId(long userId){
        List<Post> userPosts = postRepository.findAllByUserId(userId);
        return PostDtoMapper.mapToPostDtos(userPosts);
    }

    public PostDto getByTitle(String title){
        return PostDtoMapper.mapToPostDto(postRepository.findByTitle(title));
    }

    public boolean exist(Post post){
        return postRepository.existsById(post.getId());
    }
}
