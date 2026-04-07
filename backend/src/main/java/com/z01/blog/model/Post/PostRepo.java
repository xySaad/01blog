package com.z01.blog.model.Post;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.z01.blog.annotation.AccessMethod;

public interface PostRepo extends JpaRepository<PostModel, Long> {

        @AccessMethod
        @Query("SELECT p FROM PostExtra p WHERE p.id = :id AND p.deleted = false AND p.owner.deleted = false")
        PostExtra findByIdAndDeletedFalse(long id);

        @Query("SELECT p FROM PostEntity p WHERE p.id = :id")
        PostEntity findById(long id);

        @Query("SELECT p FROM PostExtra p WHERE p.id = :id")
        PostExtra findExtraById(long id);

        @Query("SELECT p FROM PostExtra p WHERE p.account = :id AND p.deleted = false AND p.owner.deleted = false")
        List<PostExtra> findAllByAccountAndDeletedFalse(long id);

        @Query("SELECT p FROM PostExtra p WHERE p.account = :id AND p.deleted = false AND p.hidden = false AND p.owner.deleted = false AND p.isPublic = true")
        List<PostExtra> findAllByAccountAndDeletedFalseAndIsPublicTrue(long id);

        @Query("SELECT p FROM PostExtra p WHERE p.account = :id")
        List<PostExtra> findAllByAccount(long id);

        @Query("SELECT p FROM PostExtra p WHERE p.deleted = false AND p.hidden = false AND p.owner.deleted = false AND p.isPublic = true AND p.account != :id")
        List<PostExtra> findAllByDeletedFalseAndIsPublicTrueAndAccountNot(long id);

        @Query("SELECT pl.id.postId FROM PostLike pl WHERE pl.id.userId = :userId AND pl.id.postId IN :postIds")
        Set<Long> findLikedPostIds(long userId, List<Long> postIds);
}