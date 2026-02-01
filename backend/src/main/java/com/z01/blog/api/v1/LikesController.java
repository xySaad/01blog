package com.z01.blog.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.guards.AuthGuard;
import com.z01.blog.model.PostLike;
import com.z01.blog.model.Post.PostRepo;

public class LikesController {
    @RestController
    @RequestMapping("/api/v1/posts/{postId}/likes")

    static public class PostLikes extends AuthGuard {
        @Autowired
        PostLike.repo postLikesRepo;
        @Autowired
        PostRepo postRepo;

        @GetMapping
        long getLike(@CookieValue String jwt, @PathVariable long postId) {
            long userId = this.getUserId(jwt);
            postRepo.findByIdAndDeletedFalse(userId).ensureAccess(userId, true);
            var postLikeId = new PostLike.Id(userId, postId);
            return postLikesRepo.countById(postLikeId);
        }

        @PostMapping
        void addOrUpdateLike(@CookieValue String jwt, @PathVariable long postId) {
            long userId = this.getUserId(jwt);
            postRepo.findByIdAndDeletedFalse(userId).ensureAccess(userId, true);

            var postLikeId = new PostLike.Id(userId, postId);
            PostLike postLike = new PostLike();
            postLike.id = postLikeId;
            postLikesRepo.save(postLike);
        }

        @DeleteMapping
        void deleteLike(@CookieValue String jwt, @PathVariable long postId) {
            long userId = this.getUserId(jwt);
            postRepo.findByIdAndDeletedFalse(userId).ensureAccess(userId, true);

            var postLikeId = new PostLike.Id(userId, postId);
            var postLike = postLikesRepo.findById(postLikeId);
            postLikesRepo.delete(postLike);
        }
    }
}
