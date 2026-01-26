package com.z01.blog.api.v1;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.controller.Auth;
import com.z01.blog.model.Post;

import cn.hutool.core.util.IdUtil;
import jakarta.validation.Valid;

@RestController
public class Posts extends Auth {
    @Autowired
    Post.repo postRepo;

    record Request(String title, String content, boolean isPublic) {
    }

    record Response(String id, boolean isPublic, LocalDateTime updatedAt) {
    };

    @PostMapping("/api/v1/posts/")
    Response create(@RequestBody @Valid Request req, @CookieValue("jwt") String jwt) {
        Post post = new Post();
        post.id = IdUtil.getSnowflake().nextId();
        post.account = this.getAuthId(jwt);
        post.title = req.title;
        post.content = req.content;
        post.isPublic = req.isPublic;
        post.createdAt = LocalDateTime.now();
        post.updatedAt = post.createdAt; // never?
        postRepo.save(post);
        return new Response(String.valueOf(post.id), false, post.updatedAt);
    }

    @PostMapping("/api/v1/posts/{id}")
    Response update(@RequestBody @Valid Post post, @CookieValue("jwt") String jwt, @PathVariable long id) {
        Optional<Post> oldPostOpt = postRepo.findById(id);
        if (oldPostOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Post oldPost = oldPostOpt.get();
        if (oldPost.account != this.getAuthId(jwt))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        oldPost.title = post.title;
        oldPost.content = post.content;
        oldPost.isPublic = post.isPublic;
        oldPost.updatedAt = LocalDateTime.now();
        Post newPost = postRepo.save(oldPost);
        return new Response(String.valueOf(newPost.id), newPost.isPublic, newPost.updatedAt);
    }

    @GetMapping("/api/v1/posts")
    List<Post> getAll(@CookieValue("jwt") String jwt) {
        return postRepo.findAll();
    }

    @GetMapping("/api/v1/posts/{id}")
    Optional<Post> getById(@CookieValue("jwt") String jwt, @PathVariable long id) {
        return postRepo.findById(id);
    }
}
