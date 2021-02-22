package com.fiedormichal.postrestapi.service;

import com.fiedormichal.postrestapi.exception.NoContentException;
import com.fiedormichal.postrestapi.exception.PostNotFoundException;
import com.fiedormichal.postrestapi.dto.PostDto;
import com.fiedormichal.postrestapi.exception.PostTitleNotFoundException;
import com.fiedormichal.postrestapi.mapper.PostDtoMapper;
import com.fiedormichal.postrestapi.mapper.JsonPostMapper;
import com.fiedormichal.postrestapi.model.Post;
import com.fiedormichal.postrestapi.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger LOG = LogManager.getLogger();

    @Scheduled(cron = "0 0 12 * * ?")
    public void updatePostsInDataBase() throws IOException {
        List<Post> actualPostsFromAPI =
                jsonPostMapper.getMappedPostsList(API_URL);
        if(postRepository.count()==0){
            saveAll(actualPostsFromAPI);
            LOG.info("Saving posts in database has finished successfully");
        }else {
            updateCurrentPostsInDataBase(actualPostsFromAPI);
            LOG.info("Updating posts in database has finished successfully");

        }
    }

    private void saveAll(List<Post> posts) {
        posts.stream().forEach(post -> postRepository.save(post));
    }

    private void updateCurrentPostsInDataBase(List<Post> actualPostsFromAPI) {
        List<Post> postsFromDataBase = postRepository.findAll();
        for (Post postFromDataBase : postsFromDataBase) {
            if (!postFromDataBase.isUpdatedByUser()) {
                Post actualPostToSaveInDataBase = findActualPost(postFromDataBase, actualPostsFromAPI);
                    postRepository.save(actualPostToSaveInDataBase);
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

    public PostDto editPost(Post post) throws PostNotFoundException {
        prepareEditedPostToSave(post);
        LOG.info("Post with id: " + post.getId() + " has prepared for saving successfully.");
        postRepository.save(post);
        LOG.info("Edited post with id: " + post.getId() + " has saved successfully.");
        return PostDtoMapper.mapToPostDto(post);
    }

    private Post prepareEditedPostToSave(Post post) throws PostNotFoundException {
        LOG.info("Searching for a post with id: " + post.getId() + " which will be edited.");
        Post postFromDataBase = postRepository.findById(post.getId())
                .orElseThrow(() -> new PostNotFoundException("Post with ID: " + post.getId() + " does not exist."));
        LOG.info("Post with id \""+ post.getId() + "\" has been found.");
        post.setUpdatedByUser(true);
        post.setUserId(postFromDataBase.getUserId());
        if(post.getTitle()==null){
            post.setTitle(postFromDataBase.getTitle());
        }
        if(post.getBody()==null){
            post.setBody(postFromDataBase.getBody());
        }
        return post;
    }

    public List<PostDto> findAllPosts() {
        LOG.info("Searching for posts from database.");
        List<Post> posts = postRepository.findAll();
        if(posts.size()==0){
            throw new NoContentException("Posts database is empty.");
        }
        LOG.info("All posts from database have found successfully");
        return PostDtoMapper.mapToPostDtos(posts);
    }

    public void deletePost(long postId) {
        LOG.info("Searching for a post with id: " + postId + " which will be deleted.");
        Post postToDelete = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID: " + postId + " does not exist."));
        LOG.info("Post with id \""+ postId + "\" has been deleted.");
        postRepository.delete(postToDelete);
    }

    public PostDto getPostByTitle(String title) {
        LOG.info("Searching for a post with title: " + title);
        Post post = postRepository.findByTitle(title).orElseThrow(()->
                new PostTitleNotFoundException("Post with title: " + title + " does not exist."));
        LOG.info("Post with title \""+ title + "\" has been found.");
        return PostDtoMapper.mapToPostDto(post);
    }

}
