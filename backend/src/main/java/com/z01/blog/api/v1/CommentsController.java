package com.z01.blog.api.v1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.z01.blog.annotation.Auth;
import com.z01.blog.annotation.EntityAccess;
import com.z01.blog.annotation.EntityAccess.Mode;
import com.z01.blog.model.Comment.CommentExtra;
import com.z01.blog.model.Comment.CommentModel;
import com.z01.blog.model.Comment.CommentRepo;
import com.z01.blog.model.Post.PostModel;

import cn.hutool.core.util.IdUtil;

@RestController
public class CommentsController {
    @Autowired
    CommentRepo commentRepo;

    public record CommentReq(String content) {
    }

    @GetMapping("/api/v1/posts/{post}/comments")
    List<CommentExtra> getPostComments(@EntityAccess(mode = Mode.Read) PostModel post) {
        return commentRepo.findAllByPostAndDeletedFalse(post.id);
    }

    @PostMapping("/api/v1/posts/{post}/comments")
    CommentModel create(@Auth.User long userId,
            @EntityAccess(mode = Mode.Read) PostModel post,
            @RequestBody CommentReq body) {

        CommentModel comment = new CommentModel();
        comment.id = IdUtil.getSnowflake().nextId();
        comment.account = userId;
        comment.post = post.id;
        comment.content = body.content;
        return commentRepo.save(comment);
    }

    @DeleteMapping("/api/v1/comments/{comment}")
    void delete(@EntityAccess(mode = Mode.Write) CommentModel comment) {
        comment.deleted = true;
        commentRepo.save(comment);
    }

    @PutMapping("/api/v1/comments/{comment}")
    void update(@EntityAccess(mode = Mode.Write) CommentModel comment, @RequestBody CommentReq body) {
        comment.content = body.content;
        commentRepo.save(comment);
    }
}
