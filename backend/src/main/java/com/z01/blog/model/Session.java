package com.z01.blog.model;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    public long accountId;
    public String jwt;
    public LocalDateTime createdAt;

    public interface repo extends JpaRepository<Session, Long> {
    }
}
