package com.z01.blog.model.Post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepo extends JpaRepository<PostModel<?>, Long> {
    PostExtra findByIdAndDeletedFalse(long id);

    List<PostExtra> findAllByAccountAndDeletedFalse(long id);

    List<PostExtra> findAllByAccountAndDeletedFalseAndIsPublicTrue(long id);

    List<PostExtra> findAllByDeletedFalseAndIsPublicTrueAndAccountNot(long id);
}