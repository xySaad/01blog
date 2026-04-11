package com.z01.blog.model.Post;

import java.time.LocalDateTime;

import com.z01.blog.annotation.EntityAccess.Mode;
import com.z01.blog.exception.AppError;
import com.z01.blog.model.BaseEntity;
import com.z01.blog.model.Audit.Hideable;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class PostModel extends BaseEntity implements Hideable {
    public String title;
    public String content;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    @Column(name = "public")
    public boolean isPublic;
    public boolean hidden;

    @Override
    public void ensureAccess(Long userId, Mode mode) {
        super.ensureAccess(userId, isPublic ? mode : Mode.Write);
        if (this.hidden && this.account != userId)
            throw AppError.ACCESS_DENIED.asException();
    }

    @Override
    public void hide() {
        this.hidden = true;
    }
}
