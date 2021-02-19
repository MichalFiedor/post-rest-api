package com.fiedormichal.postrestapi.repository;

import com.fiedormichal.postrestapi.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("Select p from Post p where p.userId=:userId and p.isDeleted=false")
    List<Post> findAllByUserId(@Param("userId") long userId);

    @Query("Select p from Post p where p.isDeleted=false")
    List<Post> findAll();

    Post findByTitle(String title);
}
