package com.z01.blog.model.Post;

import org.hibernate.annotations.Formula;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "posts")
public class PostExtra extends PostModel<PostExtra> {
    @Formula("(SELECT u.login FROM users u WHERE u.account_id = account)")
    public String accountName;

    @Formula("(SELECT COUNT(*) FROM post_likes pl WHERE pl.post_id = id)")
    public long likesCount;

    @Formula("(SELECT COUNT(*) FROM comments c WHERE c.post = id AND c.deleted = false)")
    public long commentsCount;
}
