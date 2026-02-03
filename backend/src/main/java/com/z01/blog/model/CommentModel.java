package com.z01.blog.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.annotation.AccessMethod;
import com.z01.blog.annotation.EntityAccess.Mode;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Entity
@Table(name = "comments")
public class CommentModel extends BaseEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    public long post;
    public String content;

    public static interface WithUser {
        CommentModel getComment();

        UserModel getUser();
    }

    @Override
    public void ensureAccess(Long userId, Mode accessMode) {
        var parentPost = RepoRegistry.postRepo.findByIdAndDeletedFalse(post);
        if (parentPost == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        parentPost.ensureAccess(userId, Mode.Read);
        super.ensureAccess(userId, accessMode);
    };

    public interface repo extends JpaRepository<CommentModel, Long> {
        @Query("""
                SELECT c as comment, u as user FROM CommentModel c
                JOIN UserModel u ON c.account = u.id
                WHERE c.post = :postId AND c.deleted = false""")
        List<WithUser> findAllByPostAndDeletedFalse(long postId);

        @AccessMethod
        CommentModel findByIdAndDeletedFalse(long commentId);
    }
}
