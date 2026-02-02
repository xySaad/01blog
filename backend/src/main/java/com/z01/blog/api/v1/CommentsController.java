package com.z01.blog.api.v1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.annotation.Auth;
import com.z01.blog.model.CommentModel;
import com.z01.blog.model.Post.PostRepo;

import cn.hutool.core.util.IdUtil;

@RestController
public class CommentsController {
    @Autowired
    CommentModel.repo commentRepo;
    @Autowired
    private PostRepo postRepo;

    @GetMapping("/api/v1/posts/{postId}/comments")
    List<CommentModel.WithUser> getPostComments(@Auth.User long userId, @PathVariable long postId) {
        postRepo.findByIdAndDeletedFalse(postId).ensureAccess(userId, true);
        return commentRepo.findAllByPostAndDeletedFalse(postId);
    }

    @PostMapping("/api/v1/posts/{postId}/comments")
    CommentModel create(@Auth.User long userId, @PathVariable long postId, @RequestBody String body) {

        postRepo.findByIdAndDeletedFalse(postId).ensureAccess(userId, true);
        CommentModel comment = new CommentModel();
        comment.id = IdUtil.getSnowflake().nextId();
        comment.account = userId;
        comment.post = postId;
        comment.content = body;
        return commentRepo.save(comment);
    }

    @DeleteMapping("/api/v1/comments/{commentId}")
    void delete(@Auth.User long userId, @PathVariable long commentId) {
        CommentModel comment = commentRepo.findByIdAndDeletedFalse(commentId).ensureAccess(userId, false);
        comment.deleted = true;
        commentRepo.save(comment);
    }

    @PutMapping("/api/v1/comments/{commentId}")
    void update(@Auth.User long userId, @PathVariable long commentId, @RequestBody String body) {
        CommentModel comment = commentRepo.findByIdAndDeletedFalse(commentId).ensureAccess(userId, false);
        comment.content = body;
        commentRepo.save(comment);
    }
}
