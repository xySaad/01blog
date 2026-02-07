package com.z01.blog.model.Comment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.z01.blog.annotation.AccessMethod;

public interface CommentRepo extends JpaRepository<CommentModel, Long> {
    @Query("SELECT c FROM CommentExtra c WHERE c.post = :postId AND c.deleted = false")
    List<CommentExtra> findAllByPostAndDeletedFalse(long postId);

    @AccessMethod
    CommentModel findByIdAndDeletedFalse(long commentId);
}
