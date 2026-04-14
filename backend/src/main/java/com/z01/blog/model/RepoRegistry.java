package com.z01.blog.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.z01.blog.model.Post.PostRepo;
import com.z01.blog.model.RBAC.RoleRepo;

@Component
public class RepoRegistry {
    public static PostRepo postRepo;
    public static RoleRepo rolesRepo;

    @Autowired
    public RepoRegistry(PostRepo postRepo, RoleRepo rolesRepo) {
        RepoRegistry.rolesRepo = rolesRepo;
        RepoRegistry.postRepo = postRepo;
    }
}