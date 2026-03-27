package com.z01.blog.model;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
        @Query("SELECT f.id.userId FROM Follow f WHERE f.id.followerId = :followerId AND f.id.userId IN :userIdsList")
        Set<Long> findFollowedIds(long followerId, List<Long> userIdsList);

        boolean existsByIdFollowerIdAndIdUserId(long followerId, long userId);
    }
}
