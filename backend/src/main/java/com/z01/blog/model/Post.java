package com.z01.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    public long id;
    public long account;
    @NotNull
    public String title;
    @NotNull
    public String content;
    @Column(name = "public")
    public boolean is_public;
    public boolean deleted;

    public interface repo extends JpaRepository<Post, Long> {
    }
}
