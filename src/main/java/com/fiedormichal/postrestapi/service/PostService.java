package com.fiedormichal.postrestapi.service;

import com.fiedormichal.postrestapi.dto.PostDto;
import com.fiedormichal.postrestapi.dto.PostDtoMapper;
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

    @Scheduled(cron = "0 0 12 * * ?")
    public void updatePostsInDataBase() {
        String apiUrl = "https://jsonplaceholder.typicode.com/posts";
        List<Post> actualPostsFromAPI = null;
        try {
            actualPostsFromAPI = jsonPostMapper.getMappedPostsList(apiUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveActualPostsWhenDataBaseIsEmpty(actualPostsFromAPI);
        updatePostsInDataBase(actualPostsFromAPI);
    }

    private void saveActualPostsWhenDataBaseIsEmpty(List<Post> posts) {
        if (postRepository.count() == 0) {
            saveAll(posts);
        }
    }

    private void saveAll(List<Post> posts) {
        posts.stream().forEach(post -> postRepository.save(post));
    }

    private void updatePostsInDataBase(List<Post> actualPostsFromAPI) {
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

    public PostDto edit(Post post) {
        prepareEditedPostToSave(post);
        postRepository.save(post);
        return PostDtoMapper.mapToPostDto(post);
    }

    private Post prepareEditedPostToSave(Post post) {
        Post postFromDataBase = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("Post does not exist."));
        post.setUpdated(true);
        post.setUserId(postFromDataBase.getUserId());
        return post;
    }

    public List<PostDto> findAll() {
        return PostDtoMapper.mapToPostDtos(postRepository.findAll());
    }

    public void delete(long postId) {
        postRepository.deleteById(postId);
    }

    public List<PostDto> getByUserId(long userId) {
        List<Post> userPosts = postRepository.findAllByUserId(userId);
        return PostDtoMapper.mapToPostDtos(userPosts);
    }

    public PostDto getByTitle(String title) {
        return PostDtoMapper.mapToPostDto(postRepository.findByTitle(title));
    }

}
