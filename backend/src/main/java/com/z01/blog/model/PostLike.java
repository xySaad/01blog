package com.z01.blog.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "post_likes")
public class PostLike {
    @Embeddable
    static public record Id(long userId, long postId) {
    }

    @EmbeddedId
    public Id id;

    public static interface repo extends JpaRepository<PostLike, Id> {
        Optional<PostLike> findById(Id id);

        long countById(Id id);
    }
}
