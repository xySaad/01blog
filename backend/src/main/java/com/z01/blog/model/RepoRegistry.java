package com.z01.blog.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepoRegistry {
    public static Post.repo postRepo;

    @Autowired
    public RepoRegistry(Post.repo postRepo) {
        RepoRegistry.postRepo = postRepo;
    }

}