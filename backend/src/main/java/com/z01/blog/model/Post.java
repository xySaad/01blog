package com.z01.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    public long id;
    @JsonSerialize(using = ToStringSerializer.class)
    public long account;
    @NotNull
    public String title;
    @NotNull
    public String content;

    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    @Column(name = "public")
    public boolean isPublic;
    public boolean deleted;

    public interface repo extends JpaRepository<Post, Long> {
        Optional<Post> findById(long id);
    }
}
