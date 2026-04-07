package com.z01.blog.model.Report;

import org.hibernate.annotations.Formula;

import com.z01.blog.model.Audit.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "comment_reports")
@PrimaryKeyJoinColumn(name = "id")
public class CommentReport extends ReportModel implements PostRelatedReport {
    public long commentId;
    @Formula("(SELECT c.content FROM comments c WHERE c.id = comment_id)")
    public String commentContent;
    @Formula("(SELECT p.id FROM posts p JOIN comments c ON c.post = p.id WHERE c.id = comment_id)")
    private long postId;

    public long getPostId() {
        return postId;
    }

    @Override
    public MaterialRef getMaterial() {
        return new MaterialRef(Auditable.COMMENT, commentId);
    }
}