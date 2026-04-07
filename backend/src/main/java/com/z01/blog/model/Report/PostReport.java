package com.z01.blog.model.Report;

import org.hibernate.annotations.Formula;

import com.z01.blog.model.Audit.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "post_reports")
@PrimaryKeyJoinColumn(name = "id")
public class PostReport extends ReportModel implements PostRelatedReport {
    public long postId;
    @Formula("(SELECT p.title FROM posts p WHERE p.id = post_id)")
    public String postTitle;

    public long getPostId() {
        return postId;
    }

    @Override
    public MaterialRef getMaterial() {
        return new MaterialRef(Auditable.POST, postId);
    }
}