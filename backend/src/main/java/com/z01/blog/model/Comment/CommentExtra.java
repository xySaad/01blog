package com.z01.blog.model.Comment;

import com.z01.blog.model.User.UserEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments")
public class CommentExtra extends AbstractComment {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account", insertable = false, updatable = false)
    public UserEntity owner;

}
