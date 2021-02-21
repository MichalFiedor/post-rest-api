package com.fiedormichal.postrestapi.service;

import com.fiedormichal.postrestapi.exception.PostNotFoundException;
import com.fiedormichal.postrestapi.dto.PostDto;
import com.fiedormichal.postrestapi.exception.PostsUpdateFailedException;
import com.fiedormichal.postrestapi.exception.UserNotFoundException;
import com.fiedormichal.postrestapi.mapper.PostDtoMapper;
import com.fiedormichal.postrestapi.mapper.JsonPostMapper;
import com.fiedormichal.postrestapi.model.Post;
import com.fiedormichal.postrestapi.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final JsonPostMapper jsonPostMapper;
    private static final String API_URL = "https://jsonplaceholder.typicode.com/posts";

    @Scheduled(cron = "0 0 12 * * ?")
    public void updatePostsInDataBase() {
        List<Post> actualPostsFromAPI = null;
        try {
            actualPostsFromAPI = jsonPostMapper.getMappedPostsList(API_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(actualPostsFromAPI.size()==0 || actualPostsFromAPI==null){
            throw new PostsUpdateFailedException("Posts have not been updated.");
        }
        saveActualPostsWhenDataBaseIsEmpty(actualPostsFromAPI);
        updateCurrentPostsInDataBase(actualPostsFromAPI);
    }

    private void saveActualPostsWhenDataBaseIsEmpty(List<Post> posts) {
        if (postRepository.count() == 0) {
            saveAll(posts);
        }
    }

    private void saveAll(List<Post> posts) {
        posts.stream().forEach(post -> postRepository.save(post));
    }

    private void updateCurrentPostsInDataBase(List<Post> actualPostsFromAPI) {
        List<Post> postsFromDataBase = postRepository.findAll();
        for (Post postFromDataBase : postsFromDataBase) {
            if (!postFromDataBase.isUpdated()) {
                Post actualPostToSaveInDataBase = findActualPost(postFromDataBase, actualPostsFromAPI);
                if (actualPostToSaveInDataBase.getId() != 0) {
                    postRepository.save(actualPostToSaveInDataBase);
                }
            }
        }
    }

    private Post findActualPost(Post post, List<Post> actualPostsFromAPI) {
        for (Post actualPostFromAPI : actualPostsFromAPI) {
            if (actualPostFromAPI.getId() == post.getId()) {
                return actualPostFromAPI;
            }
        }
        return new Post();
    }

    public PostDto edit(Post post) throws PostNotFoundException {
        prepareEditedPostToSave(post);
        postRepository.save(post);
        return PostDtoMapper.mapToPostDto(post);
    }

    private Post prepareEditedPostToSave(Post post) throws PostNotFoundException {
        Post postFromDataBase = postRepository.findById(post.getId())
                .orElseThrow(() -> new PostNotFoundException("Post with ID: " + post.getId() + " does not exist"));
        post.setUpdated(true);
        post.setUserId(postFromDataBase.getUserId());
        if(post.getTitle()==null){
            post.setTitle(postFromDataBase.getTitle());
        }
        if(post.getBody()==null){
            post.setBody(postFromDataBase.getBody());
        }
        return post;
    }

    public List<PostDto> findAll() {
        return PostDtoMapper.mapToPostDtos(postRepository.findAll());
    }

    public void delete(long postId) {
        Post postToDelete = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID: " + postId + " does not exist"));
        postRepository.delete(postToDelete);
    }

    public List<PostDto> getByUserId(long userId) {
        List<Post> userPosts = postRepository.findAllByUserId(userId);
        if(userPosts.size()==0){
            throw new UserNotFoundException("User with ID: " + userId + " does not exist");
        }
        return PostDtoMapper.mapToPostDtos(userPosts);
    }

    public PostDto getByTitle(String title) {
        Post post = postRepository.findByTitle(title).orElseThrow(()->
                new PostNotFoundException("Post with title: " + title + " does not exist"));
        return PostDtoMapper.mapToPostDto(post);
    }

}
