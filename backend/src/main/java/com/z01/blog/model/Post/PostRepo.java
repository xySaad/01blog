package com.z01.blog.model.Post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.z01.blog.annotation.AccessMethod;

public interface PostRepo extends JpaRepository<PostModel, Long> {
    @AccessMethod
    PostExtra findByIdAndDeletedFalse(long id);

    List<PostExtra> findAllByAccountAndDeletedFalse(long id);

    List<PostExtra> findAllByAccountAndDeletedFalseAndIsPublicTrue(long id);

    List<PostExtra> findAllByDeletedFalseAndIsPublicTrueAndAccountNot(long id);
}