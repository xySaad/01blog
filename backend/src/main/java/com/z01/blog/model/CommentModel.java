package com.z01.blog.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Entity
@Table(name = "comments")
public class CommentModel {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    public long id;
    @JsonSerialize(using = ToStringSerializer.class)
    public long account;
    @JsonSerialize(using = ToStringSerializer.class)
    public long post;
    public String content;
    public boolean deleted;

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

        Optional<CommentModel> findByIdAndAccountAndDeletedFalse(long postId, long accountId);
    }
}
