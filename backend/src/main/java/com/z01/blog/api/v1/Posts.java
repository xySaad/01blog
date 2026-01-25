package com.z01.blog.api.v1;

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

    record CreatePostResponse(String id, boolean is_public) {
    };

    @PostMapping("/api/v1/posts/")
    CreatePostResponse create(@RequestBody @Valid Post post, @CookieValue("jwt") String jwt) {
        post.id = IdUtil.getSnowflake().nextId();
        post.account = this.getAuthId(jwt);
        postRepo.save(post);
        return new CreatePostResponse(String.valueOf(post.id), false);
    }

    @PostMapping("/api/v1/posts/{id}")
    CreatePostResponse update(@RequestBody @Valid Post post, @CookieValue("jwt") String jwt, @PathVariable long id) {
        Optional<Post> oldPostOpt = postRepo.findById(id);
        if (oldPostOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Post oldPost = oldPostOpt.get();
        if (oldPost.account != this.getAuthId(jwt))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        oldPost.title = post.title;
        oldPost.content = post.content;
        oldPost.is_public = post.is_public;

        Post newPost = postRepo.save(oldPost);
        return new CreatePostResponse(String.valueOf(newPost.id), newPost.is_public);
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
