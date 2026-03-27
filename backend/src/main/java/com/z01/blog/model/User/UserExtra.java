package com.z01.blog.model.User;

import org.hibernate.annotations.Formula;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserExtra extends UserModel {
    @Formula("(SELECT COUNT(*) FROM follows f WHERE f.user_id = account_id)")
    public long followersCount;

    @Formula("(SELECT COUNT(*) FROM follows f WHERE f.follower_id = account_id)")
    public long followingCount;
}
