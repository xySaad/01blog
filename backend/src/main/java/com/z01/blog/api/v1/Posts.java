package com.z01.blog.api.v1;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.guards.AuthGuard;
import com.z01.blog.model.Post;

import cn.hutool.core.util.IdUtil;
import jakarta.validation.Valid;

@RestController
public class Posts extends AuthGuard {
    @Autowired
    Post.repo postRepo;

    record Request(String title, String content, boolean isPublic) {
    }

    record UpdateResponse(String id, boolean isPublic, LocalDateTime updatedAt) {
    };

    @PostMapping("/api/v1/posts/")
    UpdateResponse create(@RequestBody @Valid Request req, @CookieValue("jwt") String jwt) {
        Post post = new Post();
        post.id = IdUtil.getSnowflake().nextId();
        post.account = this.getUserId(jwt);
        post.title = req.title;
        post.content = req.content;
        post.isPublic = req.isPublic;
        post.createdAt = LocalDateTime.now();
        post.updatedAt = post.createdAt; // never?
        postRepo.save(post);
        return new UpdateResponse(String.valueOf(post.id), false, post.updatedAt);
    }

    @PostMapping("/api/v1/posts/{id}")
    UpdateResponse update(@RequestBody @Valid Post post, @CookieValue("jwt") String jwt, @PathVariable long id) {
        long userId = this.getUserId(jwt);

        Post oldPost = postRepo.findByIdAndDeletedFalse(id).ensureAccess(userId, false);
        oldPost.title = post.title;
        oldPost.content = post.content;
        oldPost.isPublic = post.isPublic;
        oldPost.updatedAt = LocalDateTime.now();
        Post newPost = postRepo.save(oldPost);
        return new UpdateResponse(String.valueOf(newPost.id), newPost.isPublic, newPost.updatedAt);
    }

    // feed
    @GetMapping("/api/v1/posts")
    List<Post.WithAccountName> getAll(@CookieValue("jwt") String jwt) {
        return postRepo.findAllPublicWithUsername(this.getUserId(jwt));
    }

    @GetMapping("/api/v1/posts/{postId}")
    Post getById(@CookieValue("jwt") String jwt, @PathVariable long postId) {
        long userId = this.getUserId(jwt);
        return postRepo.findByIdAndDeletedFalse(postId).ensureAccess(userId, true);
    }

    @DeleteMapping("/api/v1/posts/{id}")
    void deleteById(@CookieValue("jwt") String jwt, @PathVariable long id) {
        long userId = this.getUserId(jwt);
        Post post = postRepo.findByIdAndDeletedFalse(id).ensureAccess(userId, false);
        post.deleted = true;
        postRepo.save(post);
    }

}
