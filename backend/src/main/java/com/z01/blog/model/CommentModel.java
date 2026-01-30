package com.z01.blog.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.z01.blog.guards.RestrictedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Entity
@Table(name = "comments")
public class CommentModel extends RestrictedEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    public long post;
    public String content;

    @Override
    public CommentModel ensureAccess(long userId, boolean readOnly) {
        RepoRegistry.postRepo.findByIdAndDeletedFalse(this.post).ensureAccess(userId, true);
        super.ensureAccess(userId, readOnly);
        return this;
    }

    public static interface WithUser {
        CommentModel getComment();

        UserModel getUser();
    }

    public interface repo extends JpaRepository<CommentModel, Long> {
        @Query("""
                SELECT c as comment, u as user FROM CommentModel c
                JOIN UserModel u ON c.account = u.id
                WHERE c.post = :postId AND c.deleted = false""")
        List<WithUser> findAllByPostAndDeletedFalse(long postId);

        CommentModel findByIdAndDeletedFalse(long commentId);
    }
}
