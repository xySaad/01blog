package com.z01.blog.api.v1;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.z01.blog.annotation.Auth;
import com.z01.blog.annotation.EntityAccess;
import com.z01.blog.annotation.EntityAccess.Mode;
import com.z01.blog.model.Follow;
import com.z01.blog.model.PostLike;
import com.z01.blog.model.DTO.PostRequest;
import com.z01.blog.model.Post.PostExtra;
import com.z01.blog.model.Post.PostModel;
import com.z01.blog.model.Post.PostRepo;

import cn.hutool.core.util.IdUtil;
import jakarta.validation.Valid;

@RestController
public class PostController {
    @Autowired
    PostRepo postRepo;
    @Autowired
    PostLike.repo postLikeRepo;
    @Autowired
    Follow.repo followRepo;

    record Response(String id, boolean isPublic, LocalDateTime updatedAt) {
    };

    @PostMapping("/api/v1/posts/")
    Response create(@RequestBody @Valid PostRequest req, @Auth.User long userId) {
        PostModel post = new PostExtra();
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

    // TODO: change method to PUT
    @PostMapping("/api/v1/posts/{oldPost}")
    Response update(
            @RequestBody @Valid PostRequest post,
            @EntityAccess(mode = Mode.Write) PostModel oldPost) {

        oldPost.title = post.title;
        oldPost.content = post.content;
        oldPost.isPublic = post.isPublic;
        oldPost.updatedAt = LocalDateTime.now();
        PostModel newPost = postRepo.save(oldPost);
        return new Response(String.valueOf(newPost.id), newPost.isPublic, newPost.updatedAt);
    }

    // feed
    @GetMapping("/api/v1/posts")
    List<PostExtra> getAll(@Auth.User long userId) {
        List<PostExtra> posts = postRepo.findAllByDeletedFalseAndIsPublicTrueAndAccountNot(userId);
        List<Long> postIds = posts.stream().map(p -> p.id).toList();
        List<Long> ownerIds = posts.stream().map(p -> p.owner.accountId).toList();

        Set<Long> likedIds = postRepo.findLikedPostIds(userId, postIds);

        Set<Long> followedIds = followRepo.findFollowedIds(userId, ownerIds);

        posts.forEach(post -> {
            post.liked = likedIds.contains(post.id);
            var followed = followedIds.contains(post.owner.accountId);
            post.owner.followed = followed;
        });

        return posts;
    }

    @GetMapping("/api/v1/posts/{post}")
    PostExtra getById(@Auth.User long userId, @EntityAccess(mode = Mode.Read) PostExtra post) {
        PostLike.Id likeId = new PostLike.Id(userId, post.id);
        post.liked = postLikeRepo.existsById(likeId);
        post.owner.followed = followRepo.existsByIdFollowerIdAndIdUserId(userId, post.account);
        return post;
    }

    @DeleteMapping("/api/v1/posts/{post}")
    void deleteById(@EntityAccess(mode = Mode.Write) PostModel post) {
        post.deleted = true;
        postRepo.save(post);
    }
}
