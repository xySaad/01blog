package com.z01.blog.model.Audit;

import com.z01.blog.model.Comment.CommentModel;
import com.z01.blog.model.Post.PostEntity;
import com.z01.blog.model.User.UserEntity;

public enum Auditable {
    POST(PostEntity.class),
    USER(UserEntity.class),
    COMMENT(CommentModel.class);

    public Class<?> entity;

    Auditable(Class<?> entity) {
        this.entity = entity;
    }
}
