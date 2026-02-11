package com.z01.blog.model.Report;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnTransformer;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Table(name = "reports")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ReportModel {
    @Id
    public long id;
    public long reportedBy;
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::report_reason")
    public ReportReason reason;
    public String description;
    public LocalDateTime created_at;
    public Long resolved_by;

    @Entity
    @Table(name = "post_reports")
    @PrimaryKeyJoinColumn(name = "id")
    public static class Post extends ReportModel {
        public long postId;
    }

    @Entity
    @Table(name = "comment_reports")
    @PrimaryKeyJoinColumn(name = "id")
    public static class Comment extends ReportModel {
        public long commentId;
    }

    @Entity
    @Table(name = "user_reports")
    @PrimaryKeyJoinColumn(name = "id")
    public static class User extends ReportModel {
        public long userId;
    }
}
