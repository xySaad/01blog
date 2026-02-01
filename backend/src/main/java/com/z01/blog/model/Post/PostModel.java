package com.z01.blog.model.Post;

import java.time.LocalDateTime;

import com.z01.blog.guards.RestrictedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class PostModel<Self extends PostModel<Self>> extends RestrictedEntity<Self> {
    public String title;
    public String content;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    @Column(name = "public")
    public boolean isPublic;

    @Override
    public Self ensureAccess(long userId, boolean readOnly) {
        super.ensureAccess(userId, isPublic && readOnly);
        return (Self) this;
    }
}
