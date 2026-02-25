package com.z01.blog.model.Report;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Formula;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Table(name = "reports")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ReportModel {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    public long id;
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::report_reason")
    public ReportReason reason;
    public String description;
    public LocalDateTime createdAt;
    @Embedded
    public ReportedBy reportedBy;
    @Embedded
    public ResolvedBy resolvedBy;

    @Entity
    @Table(name = "post_reports")
    @PrimaryKeyJoinColumn(name = "id")
    public static class Post extends ReportModel {
        @Transient

        public String type = "POST";
        @JsonSerialize(using = ToStringSerializer.class)
        public long postId;

        @Formula("(SELECT p.title FROM posts p WHERE p.id = post_id)")
        public String postTitle;
    }

    @Entity
    @Table(name = "comment_reports")
    @PrimaryKeyJoinColumn(name = "id")
    public static class Comment extends ReportModel {
        @Transient
        public String type = "COMMENT";

        @JsonSerialize(using = ToStringSerializer.class)
        public long commentId;

        @JsonSerialize(using = ToStringSerializer.class)
        @Formula("(SELECT p.id FROM posts p JOIN comments c ON c.post = p.id WHERE c.id = comment_id)")
        public Long postId;

        @Formula("(SELECT p.title FROM posts p JOIN comments c ON c.post = p.id WHERE c.id = comment_id)")
        public String postTitle;
    }

    @Entity
    @Table(name = "user_reports")
    @PrimaryKeyJoinColumn(name = "id")
    public static class User extends ReportModel {
        @Transient

        public String type = "USER";
        @JsonSerialize(using = ToStringSerializer.class)
        public long userId;

        @Formula("(SELECT u.login FROM users u WHERE u.account_id = user_id)")
        public String userLogin;
    }
}
