package com.fiedormichal.postrestapi.service;

import com.fiedormichal.postrestapi.mapper.JsonPostMapper;
import com.fiedormichal.postrestapi.model.Post;
import com.fiedormichal.postrestapi.repository.PostRepository;
import lombok.RequiredArgsConstructor;
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

    public List<Post> findAll(){
        return postRepository.findAll();
    }
}
