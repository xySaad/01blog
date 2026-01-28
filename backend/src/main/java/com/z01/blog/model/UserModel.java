package com.z01.blog.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    public long accountId;

    public String login;
    public String firstName;
    public String lastName;

    public interface repo extends JpaRepository<UserModel, Long> {
        Optional<UserModel> findByAccountId(long accountId);
    }
}