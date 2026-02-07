package com.z01.blog.model.User;

import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import com.z01.blog.model.Post.PostExtra;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserExtra extends UserModel {
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "account", insertable = false, updatable = false)
    @SQLRestriction("deleted = false AND public = true")
    public List<PostExtra> posts;
}
