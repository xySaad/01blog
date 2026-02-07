package com.z01.blog.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.z01.blog.model.Comment.CommentRepo;
import com.z01.blog.model.Post.PostRepo;

@Component
public class RepoRegistry {
    public static PostRepo postRepo;
    public static CommentRepo commentRepo;

    @Autowired
    public RepoRegistry(PostRepo postRepo, CommentRepo commentRepo) {
        RepoRegistry.commentRepo = commentRepo;
        RepoRegistry.postRepo = postRepo;
    }
}