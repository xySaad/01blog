package com.z01.blog.api.v1;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.guards.AuthGuard;
import com.z01.blog.model.CommentModel;

import cn.hutool.core.util.IdUtil;

@RestController
public class CommentsController extends AuthGuard {
    @Autowired
    CommentModel.repo repo;

    @GetMapping("/api/v1/posts/{id}/comments")
    List<CommentModel.WithUser> getPostComments(@CookieValue("jwt") String jwt, @PathVariable long id) {
        this.getUserId(jwt);

        return repo.findAllByPostAndDeletedFalse(id);
    }

    @PostMapping("/api/v1/posts/{postId}/comments")
    CommentModel create(@CookieValue("jwt") String jwt, @PathVariable long postId, @RequestBody String body) {
        long userId = this.getUserId(jwt);
        CommentModel comment = new CommentModel();
        comment.id = IdUtil.getSnowflake().nextId();
        comment.account = userId;
        comment.post = postId;
        comment.content = body;
        return repo.save(comment);
    }

    @DeleteMapping("/api/v1/comments/{commentId}")
    void delete(@CookieValue("jwt") String jwt, @PathVariable long commentId) {
        Optional<CommentModel> commentOpt = repo.findByIdAndAccountAndDeletedFalse(commentId, this.getUserId(jwt));
        if (commentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        CommentModel comment = commentOpt.get();
        comment.deleted = true;
        repo.save(comment);
    }

    @PutMapping("/api/v1/comments/{commentId}")
    void update(@CookieValue("jwt") String jwt, @PathVariable long commentId, @RequestBody String body) {
        long userId = this.getUserId(jwt);
        System.out.println(userId);
        Optional<CommentModel> commentOpt = repo.findByIdAndAccountAndDeletedFalse(commentId, userId);
        if (commentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        CommentModel comment = commentOpt.get();
        comment.content = body;

        repo.save(comment);
    }
}
