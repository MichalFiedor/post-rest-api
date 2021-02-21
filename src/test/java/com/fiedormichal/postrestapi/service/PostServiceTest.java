package com.fiedormichal.postrestapi.service;

import com.fiedormichal.postrestapi.dto.PostDto;
import com.fiedormichal.postrestapi.exception.NoContentException;
import com.fiedormichal.postrestapi.exception.PostNotFoundException;
import com.fiedormichal.postrestapi.mapper.JsonPostMapper;
import com.fiedormichal.postrestapi.model.Post;
import com.fiedormichal.postrestapi.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private JsonPostMapper jsonPostMapper;

    @MockBean
    private PostRepository postRepository;

    @Test
    void find_by_title_should_return_post(){
        //given
        String title = "Test title";
        Post post = new Post(1, 2, title, "Body for test");
        PostDto postDto = PostDto.builder()
                .id(1)
                .title(title)
                .body("Body for test")
                .build();
        when(postRepository.findByTitle(title)).thenReturn(Optional.of(post));
        //when
        PostDto postDtoResult = postService.getPostByTitle(title);
        //then
        assertEquals(postDto.getId(), postDtoResult.getId());
        assertEquals(postDto.getTitle(), postDtoResult.getTitle());
        assertEquals(postDto.getBody(), postDtoResult.getBody());
    }

    @Test
    void find_post_by_title_should_throw_post_not_found_exception(){
        //given
        String title = "Test title";
        String expectedMessage = "Post with title: Test title does not exist.";
        when(postRepository.findByTitle(title)).thenReturn(Optional.empty());
        //when;
        Exception exception = assertThrows(PostNotFoundException.class, ()->postService.getPostByTitle(title));
        //then
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void delete_post_should_delete_post_by_post_id(){
        //given
        Post post = new Post(1,2, "title", "body");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        //when
        postService.deletePost(1L);
        //then
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void find_all_posts_should_return_postsDto_list(){
        //given
        Post post = new Post();
        List<Post> posts = Arrays.asList(post);
        when(postRepository.findAll()).thenReturn(posts);
        //when
        List<PostDto> result = postService.findAllPosts();
        //then
        assertEquals(1, result.size());
        assertEquals(PostDto.class, result.get(0).getClass());
    }

    @Test
    void find_all_should_throw_no_content_exception(){
        //given
        List<Post> posts = new ArrayList<>();
        when(postRepository.findAll()).thenReturn(posts);
        String expectedMessage = "Posts database is empty.";
        //when
        Exception exception = assertThrows(NoContentException.class, ()->postService.findAllPosts());
        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void edit_post_should_save_updated_post_with_set_correct_userId_and_isUpdated(){
        //given
        Post editedPost = new Post(2, "New title", "New body");

        Post oldPostFromDataBase = new Post(2, 1,"Old title","Old body");

        when(postRepository.findById(2L)).thenReturn(Optional.of(oldPostFromDataBase));
        //when
        PostDto result = postService.editPost(editedPost);
        //then
        assertEquals(1, editedPost.getUserId());
        assertTrue(editedPost.isUpdatedByUser());
        assertNotNull(result);
    }

    @Test
    void edit_post_should_save_post_with_set_correct_userId_and_isUpdated_when_the_user_has_not_changed_title_and_body(){
        //given
        Post editedPost = new Post(3,2,"title", "body");
        Post oldPostFromDataBase = new Post(3, 4, "Not edited title","Not edited body");

        when(postRepository.findById(3L)).thenReturn(Optional.of(oldPostFromDataBase));
        //when
        PostDto result = postService.editPost(editedPost);
        //then
        assertEquals(4, editedPost.getUserId());
        assertTrue(editedPost.isUpdatedByUser());
        assertEquals("Not edited title", editedPost.getTitle());
        assertEquals("Not edited body", editedPost.getBody());
        assertNotNull(result);
    }

    @Test
    void edit_post_should_throw_post_not_found_exception(){
        //given
        Post editedPost = new Post();
        editedPost.setId(2);
        when(postRepository.findById(2L)).thenReturn(Optional.empty());
        String expectedMessage = "Post with ID: 2 does not exist.";
        //when
        Exception exception = assertThrows(PostNotFoundException.class, ()->postService.editPost(editedPost));
        //then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void update_posts_in_data_base_should_save_posts_from_api_when_database_is_empty() throws IOException {
        //given
        List<Post> emptyDataBase = new ArrayList<>();
        when(postRepository.findAll()).thenReturn(emptyDataBase);
        Post post1 = mock(Post.class);
        Post post2 = mock(Post.class);
        List<Post> postsFromApi = Arrays.asList(post1, post2);
        String url = "https://jsonplaceholder.typicode.com/posts";
        when(jsonPostMapper.getMappedPostsList(url)).thenReturn(postsFromApi);
        when(postRepository.count()).thenReturn(0L);

        //when
        postService.updatePostsInDataBase();
        //then
        verify(postRepository, times(1)).save(post1);
        verify(postRepository, times(1)).save(post2);
    }

    @Test
    void update_posts_in_data_base_should_save_posts_from_api_when_database_is_not_empty() throws IOException {
        //given
        List<Post> postsFromDataBase = new ArrayList<>();
        Post post1 = new Post(1,1,"title1", "body1");
        Post post2 = new Post(2,2,"title2", "body2");
        Post postWhichWillNotBeUpdated = new Post(3,3,"title3", "body3");
        postWhichWillNotBeUpdated.setUpdatedByUser(true);
        postsFromDataBase.add(post1);
        postsFromDataBase.add(post2);
        postsFromDataBase.add(postWhichWillNotBeUpdated);

        when(postRepository.findAll()).thenReturn(postsFromDataBase);

        Post post1FromApi = new Post(1,1,"updated title1", "updated body1");
        Post post2FromApi = new Post(2,2, "updated title2", "updated body2");
        Post post3FromApi = new Post(3,3, "updated title3", "updated body3");


        List<Post> postsFromApi = new ArrayList<>();
        postsFromApi.add(post1FromApi);
        postsFromApi.add(post2FromApi);
        postsFromApi.add(post3FromApi);
        String url = "https://jsonplaceholder.typicode.com/posts";
        when(jsonPostMapper.getMappedPostsList(url)).thenReturn(postsFromApi);
        when(postRepository.count()).thenReturn(3L);
        //when
        postService.updatePostsInDataBase();
        //then
        verify(postRepository, times(1)).save(post1FromApi);
        verify(postRepository, times(1)).save(post2FromApi);
        verify(postRepository, times(0)).save(post3FromApi);
    }


}