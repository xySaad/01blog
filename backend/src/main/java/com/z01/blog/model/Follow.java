package com.z01.blog.model;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "follows")
public class Follow {
    @EmbeddedId
    public Id id;

    @Embeddable
    static public class Id {
        public long userId;
        public long followerId;
    }

    static public interface repo extends JpaRepository<Follow, Id> {

    }
}
