package com.z01.blog.model;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserModel {
    @Id
    public long accountId;

    public String login;
    public String firstName;
    public String lastName;

    public interface repo extends JpaRepository<UserModel, Long> {
    }
}