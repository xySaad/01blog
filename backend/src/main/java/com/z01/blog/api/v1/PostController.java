package com.z01.blog.api.v1;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.z01.blog.annotation.Auth;
import com.z01.blog.model.Post.PostExtra;
import com.z01.blog.model.Post.PostModel;
import com.z01.blog.model.Post.PostRepo;

import cn.hutool.core.util.IdUtil;
import jakarta.validation.Valid;

@RestController
public class PostController {
    @Autowired
    PostRepo postRepo;

    record Request(String title, String content, boolean isPublic) {
    }

    record Response(String id, boolean isPublic, LocalDateTime updatedAt) {
    };

    @PostMapping("/api/v1/posts/")
    Response create(@RequestBody @Valid Request req, @Auth.User long userId) {
        PostModel<?> post = new PostExtra();
        post.id = IdUtil.getSnowflake().nextId();
        post.account = userId;
        post.title = req.title;
        post.content = req.content;
        post.isPublic = req.isPublic;
        post.createdAt = LocalDateTime.now();
        post.updatedAt = post.createdAt; // never?
        postRepo.save(post);
        return new Response(String.valueOf(post.id), false, post.updatedAt);
    }

    @PostMapping("/api/v1/posts/{id}")
    Response update(
            @RequestBody @Valid PostExtra post,
            @Auth.User long userId,
            @PathVariable long id) {

        PostModel<?> oldPost = postRepo.findByIdAndDeletedFalse(id).ensureAccess(userId, false);
        oldPost.title = post.title;
        oldPost.content = post.content;
        oldPost.isPublic = post.isPublic;
        oldPost.updatedAt = LocalDateTime.now();
        PostModel<?> newPost = postRepo.save(oldPost);
        return new Response(String.valueOf(newPost.id), newPost.isPublic, newPost.updatedAt);
    }

    // feed
    @GetMapping("/api/v1/posts")
    List<PostExtra> getAll(@Auth.User long userId) {
        return postRepo.findAllByDeletedFalseAndIsPublicTrueAndAccountNot(userId);
    }

    @GetMapping("/api/v1/posts/{postId}")
    PostExtra getById(@Auth.User long userId, @PathVariable long postId) {
        return postRepo.findByIdAndDeletedFalse(postId).ensureAccess(userId, true);
    }

    @DeleteMapping("/api/v1/posts/{id}")
    void deleteById(@Auth.User long userId, @PathVariable long id) {
        PostModel<?> post = postRepo.findByIdAndDeletedFalse(id).ensureAccess(userId, false);
        post.deleted = true;
        postRepo.save(post);
    }
}
