package com.z01.blog.model.Post;

import org.hibernate.annotations.Formula;

import com.z01.blog.model.UserModel;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "posts")
public class PostExtra extends PostModel {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account", insertable = false, updatable = false)
    public UserModel owner;

    @Formula("(SELECT COUNT(*) FROM post_likes pl WHERE pl.post_id = id)")
    public long likesCount;

    @Formula("(SELECT COUNT(*) FROM comments c WHERE c.post = id AND c.deleted = false)")
    public long commentsCount;
}
