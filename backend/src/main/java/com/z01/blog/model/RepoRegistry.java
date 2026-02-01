package com.z01.blog.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.z01.blog.model.Post.PostRepo;

@Component
public class RepoRegistry {
    public static PostRepo postRepo;

    @Autowired
    public RepoRegistry(PostRepo postRepo) {
        RepoRegistry.postRepo = postRepo;
    }

}