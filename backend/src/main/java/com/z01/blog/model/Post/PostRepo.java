package com.z01.blog.model.Post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.z01.blog.annotation.AccessMethod;

public interface PostRepo extends JpaRepository<PostModel, Long> {
    @AccessMethod
    PostExtra findByIdAndDeletedFalse(long id);

    @Query("SELECT p FROM PostEntity p WHERE p.id = :id")
    PostEntity findById(long id);

    List<PostExtra> findAllByAccountAndDeletedFalse(long id);

    List<PostExtra> findAllByAccountAndDeletedFalseAndIsPublicTrue(long id);

    List<PostExtra> findAllByDeletedFalseAndIsPublicTrueAndAccountNot(long id);
}