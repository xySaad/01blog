package com.z01.blog.api.v1;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.annotation.Auth;
import com.z01.blog.annotation.RequiresPermission;
import com.z01.blog.exception.AppError;
import com.z01.blog.model.Follow;
import com.z01.blog.model.DTO.UserUpdateRequest;
import com.z01.blog.model.Post.PostExtra;
import com.z01.blog.model.Post.PostRepo;
import com.z01.blog.model.User.UserEntity;
import com.z01.blog.model.User.UserExtra;
import com.z01.blog.model.User.UserRepo;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PostRepo postRepo;
    @Autowired
    Follow.repo followRepo;

    @DeleteMapping
    public void delete(@Auth.Account long accountId) {
        userRepo.deleteById(accountId);
    }

    @GetMapping("{targetUserId}")
    public UserExtra get(@Auth.User long userId, @PathVariable long targetUserId) {
        var userOpt = userRepo.findExtraById(targetUserId);
        var user = userOpt.get();
        user.followed = followRepo.existsByIdFollowerIdAndIdUserId(userId, targetUserId);
        return user;
    }

    @PostMapping
    public void saveOrUpdate(@Auth.Account long accountId, @RequestBody @Valid UserUpdateRequest req) {
        if (userRepo.existsByLoginAndDeletedFalse(req.login)) {
            throw AppError.USERNAME_ALREADY_EXISTS.asException();
        }

        var user = userRepo.findByAccountIdAndDeletedFalse(accountId).orElse(new UserEntity());
        user.accountId = accountId;
        user.login = req.login;
        user.firstName = req.firstName;
        user.lastName = req.lastName;

        userRepo.save(user);
    }

    @GetMapping("{id}/posts")
    List<PostExtra> getUserPosts(@Auth.User long userId, @PathVariable long id) {
        if (id == userId)
            return postRepo.findAllByAccountAndDeletedFalse(userId);

        var posts = postRepo.findAllByAccountAndDeletedFalseAndIsPublicTrue(id);

        List<Long> postIds = posts.stream().map(p -> p.id).toList();
        Set<Long> likedIds = postRepo.findLikedPostIds(userId, postIds);
        posts.forEach(post -> post.liked = likedIds.contains(post.id));

        return posts;
    }

    @GetMapping("search/{query}")
    @RequiresPermission(scope = "v1:users:read", description = "view and search for users")
    public List<UserEntity> searchUsers(@PathVariable String query) {
        return userRepo.findTop20ByLoginStartingWithIgnoreCaseAndDeletedFalseOrderByLogin(query);
    }
}