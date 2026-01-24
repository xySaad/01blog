package com.z01.blog.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.controller.Auth;
import com.z01.blog.model.Post;

import cn.hutool.core.util.IdUtil;
import jakarta.validation.Valid;

@RestController
public class Posts extends Auth {
    @Autowired
    Post.repo postRepo;

    @PostMapping("/api/v1/posts/")
    void create(@RequestBody @Valid Post post, @CookieValue("jwt") String jwt) {
        post.id = IdUtil.getSnowflake().nextId();
        post.account = this.getAuthId(jwt);
        postRepo.save(post);
    }
}
