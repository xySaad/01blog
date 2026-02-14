package com.z01.blog.model.RBAC;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "permissions")
public class PermissionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    public String scope;
    public String description;

    @Repository
    public interface repo extends JpaRepository<PermissionModel, Long> {
        PermissionModel findByScope(String scope);
    }
}