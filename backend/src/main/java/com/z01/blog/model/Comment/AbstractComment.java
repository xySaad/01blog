package com.z01.blog.model.Comment;

import com.z01.blog.annotation.EntityAccess.Mode;
import com.z01.blog.exception.AppError;
import com.z01.blog.model.BaseEntity;
import com.z01.blog.model.RepoRegistry;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractComment extends BaseEntity {
    public long post;
    public String content;

    @Override
    public void ensureAccess(Long userId, Mode accessMode) {
        var parentPost = RepoRegistry.postRepo.findByIdAndDeletedFalse(post);
        if (parentPost == null)
            throw AppError.PARENT_POST_NOT_FOUND.asException();

        parentPost.ensureAccess(userId, Mode.Read);
        super.ensureAccess(userId, accessMode);
    };

}
