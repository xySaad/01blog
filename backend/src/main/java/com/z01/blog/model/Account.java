package com.z01.blog.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    public long id;
    public String email;
    public String passwordHash;
    public Integer verificationCode;
    public LocalDateTime codeCreatedAt;

    public interface repo extends JpaRepository<Account, Long> {
        boolean existsByEmail(String email);
    }
}
