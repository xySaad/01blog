package com.z01.blog.model.Post;

import java.time.LocalDateTime;

import com.z01.blog.annotation.EntityAccess;
import com.z01.blog.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class PostModel extends BaseEntity {
    public String title;
    public String content;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    @Column(name = "public")
    public boolean isPublic;

    @Override
    public void ensureAccess(Long userId, EntityAccess.Mode mode) {
        super.ensureAccess(userId, isPublic ? mode : EntityAccess.Mode.Write);
    }
}
