package com.z01.blog.api.v1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.z01.blog.guards.AuthGuard;
import com.z01.blog.model.CommentModel;
import com.z01.blog.model.Post;

import cn.hutool.core.util.IdUtil;

@RestController
public class CommentsController extends AuthGuard {
    @Autowired
    CommentModel.repo commentRepo;
    @Autowired
    private Post.repo postRepo;

    @GetMapping("/api/v1/posts/{postId}/comments")
    List<CommentModel.WithUser> getPostComments(@CookieValue("jwt") String jwt, @PathVariable long postId) {
        long userId = this.getUserId(jwt);
        postRepo.findByIdAndDeletedFalse(postId).ensureAccess(userId, true);
        return commentRepo.findAllByPostAndDeletedFalse(postId);
    }

    @PostMapping("/api/v1/posts/{postId}/comments")
    CommentModel create(@CookieValue("jwt") String jwt, @PathVariable long postId, @RequestBody String body) {
        long userId = this.getUserId(jwt);

        postRepo.findByIdAndDeletedFalse(postId).ensureAccess(userId, true);
        CommentModel comment = new CommentModel();
        comment.id = IdUtil.getSnowflake().nextId();
        comment.account = userId;
        comment.post = postId;
        comment.content = body;
        return commentRepo.save(comment);
    }

    @DeleteMapping("/api/v1/comments/{commentId}")
    void delete(@CookieValue("jwt") String jwt, @PathVariable long commentId) {
        long userId = this.getUserId(jwt);

        CommentModel comment = commentRepo.findByIdAndDeletedFalse(commentId).ensureAccess(userId, false);
        comment.deleted = true;
        commentRepo.save(comment);
    }

    @PutMapping("/api/v1/comments/{commentId}")
    void update(@CookieValue("jwt") String jwt, @PathVariable long commentId, @RequestBody String body) {
        long userId = this.getUserId(jwt);

        CommentModel comment = commentRepo.findByIdAndDeletedFalse(commentId).ensureAccess(userId, false);
        comment.content = body;
        commentRepo.save(comment);
    }
}
