package com.z01.blog.model.Post;

import java.util.List;
import java.util.Set;

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

    @Query("SELECT pl.id.postId FROM PostLike pl WHERE pl.id.userId = :userId AND pl.id.postId IN :postIds")
    Set<Long> findLikedPostIds(long userId, List<Long> postIds);
}