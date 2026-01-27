package com.z01.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    public long id;
    @JsonSerialize(using = ToStringSerializer.class)
    public long account;
    @NotNull
    public String title;
    @NotNull
    public String content;

    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    @Column(name = "public")
    public boolean isPublic;
    public boolean deleted;

    public interface WithAccountName {
        @JsonSerialize(using = ToStringSerializer.class)
        long getId();

        @JsonSerialize(using = ToStringSerializer.class)
        long getAccount();

        String getTitle();

        String getContent();

        LocalDateTime getCreatedAt();

        LocalDateTime getUpdatedAt();

        boolean getIsPublic();

        String getAccountName();
    }

    public interface repo extends JpaRepository<Post, Long> {
        Optional<Post> findById(long id);

        Optional<Post> findByIdAndDeletedFalse(long id);

        List<Post> findAllByAccountAndDeletedFalse(long id);

        List<Post> findAllByAccountAndDeletedFalseAndIsPublicTrue(long id);

        List<Post> findAllByDeletedFalse();

        @Query("""
                SELECT p.id AS id, p.account AS account, p.title AS title,
                p.content AS content, p.createdAt AS createdAt, p.updatedAt AS updatedAt,
                p.isPublic AS isPublic, u.login AS accountName
                FROM Post p JOIN UserModel u ON p.account = u.accountId
                WHERE p.deleted = false AND p.isPublic = true AND p.account != ?1""")
        List<WithAccountName> findAllPublicWithUsername(long id);
    }
}
