package com.z01.blog.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.annotation.Auth;
import com.z01.blog.model.Follow;

@RestController
@RequestMapping("/api/v1/follow/")
public class FollowController {
    @Autowired
    Follow.repo repo;

    @PostMapping("{userId}")
    void followUser(@Auth.User long followerId, @PathVariable long userId) {
        var follow = new Follow();
        follow.id = new Follow.Id();
        follow.id.userId = userId;
        follow.id.followerId = followerId;
        repo.save(follow);
    }

    @DeleteMapping("{userId}")
    void delete(@Auth.User long followerId, @PathVariable long userId) {
        var follow = new Follow();
        follow.id = new Follow.Id();
        follow.id.userId = userId;
        follow.id.followerId = followerId;
        repo.delete(follow);
    }
}
